package com.mayreh.typo.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingProblem {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Token {
        int tokenIndex;
        int endOffset;
        String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mora {
        int tokenIndex;
        String text;
    }

    List<Token> tokens;
    List<Mora> moras;
    int allHiraganasCount;

    public String toJSON() {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static TypingProblem fromJSON(String json) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            final TypingProblem problem = mapper.readValue(json, TypingProblem.class);

            int count = 0;
            for (Mora mora : problem.moras) {
                count += mora.text.length();
            }
            problem.setAllHiraganasCount(count);
            return problem;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String prettyString() {
        final StringBuilder builder = new StringBuilder();

        int tokenIndex = -1;
        for (Mora mora : moras) {
            if (tokenIndex != mora.tokenIndex) {
                tokenIndex = mora.tokenIndex;
                builder.append('\n');
                builder.append(tokenIndex).append(':');
                builder.append(tokens.get(tokenIndex).text);
                builder.append(" = ");
            }

            builder.append(mora.text);
        }

        return builder.toString();
    }
}
