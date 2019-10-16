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
    private final AtomicInteger shortProblemPointer = new AtomicInteger(0);
    private final AtomicInteger longProblemPointer = new AtomicInteger(0);

    private final List<TypingProblem> shortProblems = new ArrayList<>();
    private final List<TypingProblem> longProblems = new ArrayList<>();

    @PostConstruct
    void init() throws Exception {
        Files.lines(Paths.get(this.getClass()
                .getClassLoader().getResource("problem/all.json").toURI())).forEach(line -> {
            final TypingProblem problem = TypingProblem.fromJSON(line.trim());
            switch (problem.getType()) {
                case SHORT:
                    shortProblems.add(problem);
                    break;
                case LONG:
                    longProblems.add(problem);
                    break;
                default:
                    break;
            }
        });
    }

    public TypingProblem nextShortProblem() {
        return shortProblems.get(
                shortProblemPointer.updateAndGet(i -> (i + 1) % shortProblems.size()));
    }

    public TypingProblem nextLongProblem() {
        return longProblems.get(
                longProblemPointer.updateAndGet(i -> (i + 1) % longProblems.size()));
    }
}
