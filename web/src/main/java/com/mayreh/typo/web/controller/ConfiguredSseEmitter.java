package com.mayreh.typo.web.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class ConfiguredSseEmitter extends SseEmitter {
    public ConfiguredSseEmitter() {
        super(60 * 60 * 1000L);
    }

    @Override
    protected void extendResponse(ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);

        final HttpHeaders headers = outputMessage.getHeaders();
        headers.set("X-Accel-Buffering", "no");
    }
}
