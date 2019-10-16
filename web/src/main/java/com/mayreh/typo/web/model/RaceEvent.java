package com.mayreh.typo.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceEvent {
    public enum EventType {
        COUNTDOWN,
        START,
        FINISHED,
        PLAYERS_CHANGED,
        INPUT,
    }

    EventType eventType;

    Object content;
}
