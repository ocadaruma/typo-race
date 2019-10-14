package com.mayreh.typo.model;

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
}
