package com.mayreh.typo.web.tool;

import com.mayreh.typo.core.TypingProblem;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ProblemGeneratorTest {
    public static final String SOURCE_TEXT = "青空文庫の使い方と約束事を紹介しています。初めての方、ファイルやキャプチャーの取り扱いについて知りたい方も、こちらへどうぞ。";

    @Test
    public void testGenerate() {
        final ProblemGenerator generator = new ProblemGenerator();

        final TypingProblem result = generator.generate(SOURCE_TEXT, Collections.emptyList());

        assertThat(result.getTokens()).hasSize(33);

        assertThat(result.getTokens().get(0)).isEqualTo(
                new TypingProblem.Token(0, 2, "青空"));
        assertThat(result.getTokens().get(3)).isEqualTo(
                new TypingProblem.Token(3, 8, "使い方"));
        assertThat(result.getTokens().get(13)).isEqualTo(
                new TypingProblem.Token(13, 21, "。"));
        assertThat(result.getTokens().get(16)).isEqualTo(
                new TypingProblem.Token(16, 26, "方"));
        assertThat(result.getTokens().get(31)).isEqualTo(
                new TypingProblem.Token(31, 61, "どうぞ"));
        assertThat(result.getTokens().get(32)).isEqualTo(
                new TypingProblem.Token(32, 62, "。"));

        assertThat(result.getMoras()).hasSize(74);

        assertThat(result.getMoras().get(0)).isEqualTo(
                new TypingProblem.Mora(0, "あ"));
        assertThat(result.getMoras().get(3)).isEqualTo(
                new TypingProblem.Mora(0, "ら"));
        assertThat(result.getMoras().get(36)).isEqualTo(
                new TypingProblem.Mora(16, "ほ"));
        assertThat(result.getMoras().get(37)).isEqualTo(
                new TypingProblem.Mora(16, "う"));
        assertThat(result.getMoras().get(73)).isEqualTo(
                new TypingProblem.Mora(32, "。"));
    }

    @Test
    public void testGenerateWithHint() {
        final ProblemGenerator generator = new ProblemGenerator();

        final TypingProblem result = generator.generate(
                SOURCE_TEXT,
                Collections.singletonList(new ProblemGenerator.ReadingHint(16, "カタ")));

        assertThat(result.getTokens()).hasSize(33);

        assertThat(result.getTokens().get(0)).isEqualTo(
                new TypingProblem.Token(0, 2, "青空"));
        assertThat(result.getTokens().get(3)).isEqualTo(
                new TypingProblem.Token(3, 8, "使い方"));
        assertThat(result.getTokens().get(13)).isEqualTo(
                new TypingProblem.Token(13, 21, "。"));
        assertThat(result.getTokens().get(16)).isEqualTo(
                new TypingProblem.Token(16, 26, "方"));
        assertThat(result.getTokens().get(31)).isEqualTo(
                new TypingProblem.Token(31, 61, "どうぞ"));
        assertThat(result.getTokens().get(32)).isEqualTo(
                new TypingProblem.Token(32, 62, "。"));

        assertThat(result.getMoras()).hasSize(74);

        assertThat(result.getMoras().get(0)).isEqualTo(
                new TypingProblem.Mora(0, "あ"));
        assertThat(result.getMoras().get(3)).isEqualTo(
                new TypingProblem.Mora(0, "ら"));
        assertThat(result.getMoras().get(36)).isEqualTo(
                new TypingProblem.Mora(16, "か"));
        assertThat(result.getMoras().get(37)).isEqualTo(
                new TypingProblem.Mora(16, "た"));
        assertThat(result.getMoras().get(73)).isEqualTo(
                new TypingProblem.Mora(32, "。"));
    }
}
