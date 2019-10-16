package com.mayreh.typo.web.model;

import com.mayreh.typo.core.TypingProblem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

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

    private List<Player> players;

    private TypingProblem problem;
}
