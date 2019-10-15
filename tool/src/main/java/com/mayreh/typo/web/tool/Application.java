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
import java.util.Collections;

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
                "メロスは激怒した。必ず、かの邪智暴虐の王を除かなければならぬと決意した。メロスには政治がわからぬ。メロスは、村の牧人である。笛を吹き、羊と遊んで暮して来た。けれども邪悪に対しては、人一倍に敏感であった。きょう未明メロスは村を出発し、野を越え山越え、十里はなれた此のシラクスの市にやって来た。メロスには父も、母も無い。女房も無い。十六の、内気な妹と二人暮しだ。この妹は、村の或る律気な一牧人を、近々、花婿として迎える事になっていた。",
                Arrays.asList(
                        new ReadingHint(88, "シ"),
                        new ReadingHint(89, "ラ"),
                        new ReadingHint(90, "ク"),
                        new ReadingHint(91, "ス")
                ));
        log.info("generate. {}", problem.prettyString());

        try(PrintWriter writer = new PrintWriter(new FileWriter(output, true))) {
            writer.println(problem.toJSON());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
