package com.mayreh.typo.web.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mayreh.typo.core.TypingProblem;
import com.mayreh.typo.web.model.Player;
import com.mayreh.typo.web.model.Race;
import com.mayreh.typo.web.model.RaceEvent;
import com.mayreh.typo.web.repository.TypingProblemRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@RestController
public class ApiController {
    private final TypingProblemRepository problemRepository;

    private final ConcurrentMap<String, Race> raceRepository;
    private final ConcurrentHashMap.KeySetView<RaceStreamConnection, Boolean> raceListeners;

    private final ScheduledExecutorService heartbeatExecutorService;
    private final ExecutorService countdownExecutorService;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {}

    public ApiController(TypingProblemRepository problemRepository) {
        this.problemRepository = problemRepository;

        raceRepository = new ConcurrentHashMap<>();
        raceListeners = ConcurrentHashMap.newKeySet();
        heartbeatExecutorService = Executors.newScheduledThreadPool(4,
                new ThreadFactoryBuilder().setNameFormat("heartbeat-%d").build());
        countdownExecutorService = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder().setNameFormat("countdown-%d").build());
    }

    @PostMapping("/api/race")
    public Race newRace() {
        final String raceId = UUID.randomUUID().toString();
        final TypingProblem problem = problemRepository.nextProblem();
        final Race race = new Race(
                raceId, Race.RaceState.WAITING, new ArrayList<>(), problem);

        raceRepository.put(raceId, race);

        return race;
    }

    @GetMapping("/api/race/{raceId}")
    public Race getRace(@PathVariable("raceId") String raceId) {
        final Race race = raceRepository.get(raceId);
        if (race == null) {
            throw new NotFoundException();
        }
        return race;
    }

    @GetMapping("/api/race/{raceId}/players")
    public List<Player> racePlayers(@PathVariable("raceId") String raceId) {
        final Race race = raceRepository.get(raceId);
        if (raceId == null) {
            throw new NotFoundException();
        }
        return race.getPlayers();
    }

    @PostMapping("/api/race/{raceId}/join")
    public Race joinRace(
            @PathVariable("raceId") String raceId,
            @RequestBody JoinRequest request) {
        if (!raceRepository.containsKey(raceId)) {
            throw new NotFoundException();
        }

        return raceRepository.compute(raceId, (key, oldValue) -> {
            final List<Player> players = oldValue.getPlayers() == null ?
                    new ArrayList<>() : new ArrayList<>(oldValue.getPlayers());
            final Player player = new Player(request.name, 0);
            if (!players.contains(player)) {
                players.add(player);
            }
            notifyEvent(raceId, new RaceEvent(RaceEvent.EventType.PLAYERS_CHANGED, players));

            return new Race(raceId, oldValue.getState(), players, oldValue.getProblem());
        });
    }

    @PostMapping("/api/race/{raceId}/start")
    public Race startRace(@PathVariable String raceId) {
        if (!raceRepository.containsKey(raceId)) {
            throw new NotFoundException();
        }

        synchronized (this.raceRepository) {
            final Race race = raceRepository.get(raceId);
            if (race.getState() != Race.RaceState.WAITING) {
                return race;
            }

            final Race newRace = new Race(
                    race.getRaceId(), Race.RaceState.COUNTDOWN, race.getPlayers(), race.getProblem());
            raceRepository.put(raceId, newRace);

            countdownExecutorService.execute(() -> {
                int count = 10;
                while (count > 0) {
                    notifyEvent(raceId, new RaceEvent(RaceEvent.EventType.COUNTDOWN, count));
                    count--;
                }
                raceRepository.compute(raceId, (key, oldValue) -> {
                    return new Race(race.getRaceId(), Race.RaceState.PLAYING, oldValue.getPlayers(), race.getProblem());
                });
                notifyEvent(raceId, new RaceEvent(RaceEvent.EventType.START, null));
            });

            return newRace;
        }
    }

    @PostMapping("/api/race/{raceId}/input")
    public Race input(
            @PathVariable String raceId,
            @RequestBody InputRequest request) {
        final Race race = raceRepository.get(raceId);
        if (race == null) {
            throw new NotFoundException();
        }

        final Race updatedRace = raceRepository.compute(raceId, (key, oldValue) -> {
            final List<Player> players = new ArrayList<>(oldValue.getPlayers());
            int idx = -1;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getName().equals(request.name)) {
                    idx = i;
                    break;
                }
            }

            if (idx < 0) {
                return oldValue;
            }
            players.set(idx, new Player(request.name, request.doneCount));

            final Race.RaceState nextState;
            if (request.doneCount == race.getProblem().getAllHiraganasCount()) {
                nextState = Race.RaceState.FINISHED;
            } else {
                nextState = oldValue.getState();
            }

            return new Race(raceId, nextState, players, oldValue.getProblem());
        });

        notifyEvent(raceId, new RaceEvent(RaceEvent.EventType.INPUT, updatedRace.getPlayers()));
        if (race.getState() != updatedRace.getState() && updatedRace.getState() == Race.RaceState.FINISHED) {
            notifyEvent(raceId, new RaceEvent(RaceEvent.EventType.FINISHED, request.name));
        }
        return updatedRace;
    }

    @GetMapping("/api/stream/race/{raceId}")
    public SseEmitter raceStream(@PathVariable String raceId) {
        if (!raceRepository.containsKey(raceId)) {
            throw new NotFoundException();
        }

        final ConfiguredSseEmitter emitter = new ConfiguredSseEmitter();
        final RaceStreamConnection conn = new RaceStreamConnection(raceId, emitter);
        raceListeners.add(conn);

        final ScheduledFuture<?> hearbeat = heartbeatExecutorService.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("ping"));
            } catch (Exception e) {
                log.debug("An error occurred while sending heartbeat");
                raceListeners.remove(conn);
                emitter.complete();
                throw new RuntimeException(e);
            }
        }, 100L, 1000L, TimeUnit.MILLISECONDS);

        emitter.onCompletion(() -> {
            hearbeat.cancel(true);
        });

        return emitter;
    }

    private void notifyEvent(String raceId, RaceEvent event) {
        for (RaceStreamConnection listener : raceListeners) {
            if (listener.raceId.equals(raceId)) {
                try {
                    listener.emitter.send(event);
                } catch (IOException e) {
                    log.debug("failed to send countdown", e);
                }
            }
        }
    }

    @RequiredArgsConstructor
    private static class RaceStreamConnection {
        private final String raceId;
        private final ConfiguredSseEmitter emitter;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class InputRequest {
        String name;
        int doneCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class JoinRequest {
        String name;
    }
}
