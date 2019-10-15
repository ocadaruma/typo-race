package com.mayreh.typo.web.model;

import com.mayreh.typo.core.TypingProblem;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Race {
    public enum RaceState {
        WAITING,
        COUNTDOWN,
        PLAYING,
        FINISHED,
    }

    private String raceId;

    private RaceState state;

    private TypingProblem problem;
}
