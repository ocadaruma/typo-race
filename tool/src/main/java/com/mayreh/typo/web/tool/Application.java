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
                "私は三十年ほど前に、日本人は如何にして渡って来たかという題目について所感を発表したことがあるが、それからこの方、船と航海の問題が常に念頭から離れなかった。その中の一つで是非ともここに述べておきたいのは、日本と沖縄とを連ねる交通路のことである。今では沖縄へ行くのには概ね西海岸の航路を取っているが、古くは東海岸を主としていたのではないかということを説いてみたいのである。日本の南北の交通は、後に使わなくなった東海岸を余計に使っていたのではないか。古い航海には東海岸の方が便利であった。遠浅の砂浜が多く、短距離を航海しながら船を陸に上げて宿をとり、話がつけば暫くの間、あがったところに滞在することもできた。むかしは一年に一回航海すればよかったので、年内に再びやってこようなどということは考えなかったのである。",
                Arrays.asList(
                        new ReadingHint(128, "ノチ"),
                        new ReadingHint(190, "アイダ"),
                        new ReadingHint(207, "イッ")
                ));
        log.info("generate. {}", problem.prettyString());

        try(PrintWriter writer = new PrintWriter(new FileWriter(output, true))) {
            writer.println(problem.toJSON());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
