package com.mayreh.typo.web.tool;

import com.mayreh.typo.core.TypingProblem;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.ReadingAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProblemGenerator {
    @Value
    public static class ReadingHint {
        int tokenIndex;
        String reading;
    }

    @RequiredArgsConstructor
    private static class IntermediateToken {
        private final int endOffset;
        private final String text;
        private final String reading;
    }

    public TypingProblem generate(String sourceText, List<ReadingHint> hints) {
        try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(
                null, false, JapaneseTokenizer.Mode.EXTENDED)) {
            final CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
            final OffsetAttribute offsetAttribute = tokenizer.addAttribute(OffsetAttribute.class);
            final ReadingAttribute readingAttribute = tokenizer.addAttribute(ReadingAttribute.class);

            tokenizer.setReader(new StringReader(sourceText));
            tokenizer.reset();

            final List<IntermediateToken> intermediateTokens = new ArrayList<>();
            while (tokenizer.incrementToken()) {
                intermediateTokens.add(
                        new IntermediateToken(
                                offsetAttribute.endOffset(),
                                charTermAttribute.toString(),
                                readingAttribute.getReading()));
            }

            for (ReadingHint hint : hints) {
                final IntermediateToken token = intermediateTokens.get(hint.tokenIndex);
                intermediateTokens.set(hint.tokenIndex, new IntermediateToken(
                        token.endOffset,
                        token.text,
                        hint.reading
                ));
            }

            final List<TypingProblem.Token> tokens = new ArrayList<>();
            final List<TypingProblem.Mora> moras = new ArrayList<>();

            for (int i = 0; i < intermediateTokens.size(); i++) {
                final IntermediateToken intermediateToken = intermediateTokens.get(i);

                System.out.println(intermediateToken.text);
                final StringBuilder readingBuilder = new StringBuilder();
                for (char c : intermediateToken.reading.toCharArray()) {
                    if (Pattern.compile("^[ァ-ン]$").matcher(String.valueOf(c)).matches()) {
                        readingBuilder.append((char) (c - 0x60));
                    } else {
                        readingBuilder.append(c);
                    }
                }
                final String hiraganaReading = readingBuilder.toString();

                final TypingProblem.Token token = new TypingProblem.Token(
                        i, intermediateToken.endOffset, intermediateToken.text);
                tokens.add(token);

                for (MoraTokenizer.Token moraToken : MoraTokenizer.tokenize(hiraganaReading)) {
                    moras.add(new TypingProblem.Mora(i, moraToken.getMora()));
                }
            }

            return new TypingProblem(tokens, moras);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
