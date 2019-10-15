package com.mayreh.typo.web.tool;

import com.mayreh.typo.web.tool.MoraTokenizer.Token;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MoraTokenizerTest {
    @Test
    public void testTokenize() {
        assertThat(MoraTokenizer.tokenize("じゃぱりまん")).isEqualTo(Arrays.asList(
                new Token("じゃ"),
                new Token("ぱ"),
                new Token("り"),
                new Token("ま"),
                new Token("ん")
        ));

        assertThat(MoraTokenizer.tokenize("わーい、たっのしー。")).isEqualTo(Arrays.asList(
                new Token("わ"),
                new Token("ー"),
                new Token("い"),
                new Token("、"),
                new Token("た"),
                new Token("っ"),
                new Token("の"),
                new Token("し"),
                new Token("ー"),
                new Token("。")
        ));

        assertThat(MoraTokenizer.tokenize("げんざい２１じ38ぷんですにゃ")).isEqualTo(Arrays.asList(
                new Token("げ"),
                new Token("ん"),
                new Token("ざ"),
                new Token("い"),
                new Token("２"),
                new Token("１"),
                new Token("じ"),
                new Token("3"),
                new Token("8"),
                new Token("ぷ"),
                new Token("ん"),
                new Token("で"),
                new Token("す"),
                new Token("にゃ")
        ));

        assertThat(MoraTokenizer.tokenize("きゃぷちゃー")).isEqualTo(Arrays.asList(
                new Token("きゃ"),
                new Token("ぷ"),
                new Token("ちゃ"),
                new Token("ー")
        ));
    }
}
