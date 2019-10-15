package com.mayreh.typo.web.controller;

import com.mayreh.typo.core.TypingProblem;
import com.mayreh.typo.web.model.Race;
import com.mayreh.typo.web.repository.TypingProblemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
public class ApiController {
    private final TypingProblemRepository problemRepository;
    private final ConcurrentMap<String, Race> raceRepository;

    public ApiController(TypingProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
        this.raceRepository = new ConcurrentHashMap<>();
    }

    @PostMapping("/api/race")
    public Race newRace() {
        final String raceId = UUID.randomUUID().toString();
        final TypingProblem problem = problemRepository.nextProblem();
        final Race race = new Race(raceId, Race.RaceState.WAITING, problem);

        raceRepository.put(raceId, race);

        return race;
    }

    @GetMapping("/api/race/{raceId}")
    public Race getRace(@PathVariable("raceId") String raceId) {
        return raceRepository.get(raceId);
    }

//    @PostMapping("/api/race/{raceId}/start")
//    public Race startRace(@PathVariable String raceId) {
//
//    }
//
//    @GetMapping("/api/stream/race/{raceId}")
//    public SseEmitter raceStream(@PathVariable String raceId) {
//
//    }
}
