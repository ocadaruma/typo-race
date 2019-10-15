package com.mayreh.typo.web.repository;

import com.mayreh.typo.core.TypingProblem;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TypingProblemRepository {
    private final AtomicInteger pointer = new AtomicInteger(0);
    private final List<TypingProblem> problems = new ArrayList<>();

    @PostConstruct
    void init() throws Exception {
        Files.lines(Paths.get(this.getClass()
                .getClassLoader().getResource("problem/all.json").toURI())).forEach(line -> {
            final TypingProblem problem = TypingProblem.fromJSON(line.trim());
            problems.add(problem);
        });
    }

    public TypingProblem nextProblem() {
        return problems.get(pointer.updateAndGet(i -> (i + 1) % problems.size()));
    }
}
