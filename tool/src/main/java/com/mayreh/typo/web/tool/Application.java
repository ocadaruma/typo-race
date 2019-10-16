package com.mayreh.typo.web.tool;

import com.mayreh.typo.core.TypingProblem;
import com.mayreh.typo.web.tool.ProblemGenerator.ReadingHint;
import com.mayreh.typo.web.tool.prop.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.*;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties(AppProperties.class)
public class Application implements CommandLineRunner {
    private final AppProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        final ProblemGenerator generator = new ProblemGenerator();
        final File output = new File(properties.getOutputDir(), "all.json");

        final TypingProblem problem = generator.generate(
                TypingProblem.ProblemType.SHORT,
                "青空文庫の使い方と約束事を紹介しています。",
                Arrays.asList());
        log.info("generate. {}", problem.prettyString());

        try(PrintWriter writer = new PrintWriter(new FileWriter(output, true))) {
            writer.println(problem.toJSON());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
