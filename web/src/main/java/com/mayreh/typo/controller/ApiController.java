package com.mayreh.typo.controller;

import com.mayreh.typo.model.Race;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
public class ApiController {
    private final ConcurrentMap<String, Race> raceRepository;

    public ApiController() {
        this.raceRepository = new ConcurrentHashMap<>();
    }

    @PostMapping("/api/race")
    public Race newRace() {
        final String raceId = UUID.randomUUID().toString();
        final Race race = new Race(raceId, Race.RaceState.WAITING);
        raceRepository.put(raceId, race);

        return race;
    }

    @GetMapping("/api/race/{raceId}")
    public Race getRace(@PathVariable("raceId") String raceId) {
        return raceRepository.get(raceId);
    }

    @PostMapping("/api/race/{raceId}/start")
    public Race startRace(@PathVariable String raceId) {
        
    }

    @GetMapping("/api/stream/race/{raceId}")
    public SseEmitter raceStream(@PathVariable String raceId) {

    }
}
