package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.DocumentTokenizer;
import com.mmnaseri.utils.dareader.token.Token;
import com.mmnaseri.utils.dareader.token.impl.CommonTokenTypes;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/** Tests for {@link SimpleDocumentTokenizer}. */
public class SimpleDocumentTokenizerTest {

  private static DocumentTokenizer tokenizer(String text) {
    return DocumentTokenizer.with(CommonTokenTypes.registry()).create(text);
  }

  @Test
  public void noMatches() {
    DocumentTokenizer tokenizer = tokenizer("[a123abc]");

    assertThat(tokenizer.hasNext(), is(true));
    assertThat(tokenizer.next(), is(nullValue()));
    assertThat(tokenizer.cursor(), is(0));
  }

  @Test
  public void tokenPriority() {
    DocumentTokenizer tokenizer = tokenizer("123.456 abc");

    assertThat(tokenizer.hasNext(), is(true));
    assertThat(tokenizer.line(), is(1));
    assertThat(tokenizer.offset(), is(1));
    assertThat(tokenizer.cursor(), is(0));

    Token token = tokenizer.next();

    // Even though this can be an int, too, since float has a higher priority, that's what is
    // matched.
    assertThat(token, is(notNullValue()));
    assertThat(token.type(), is(CommonTokenTypes.UNSIGNED_FLOAT));
    assertThat(token.value(), is("123.456"));
    assertThat(token.length(), is(7));

    assertThat(tokenizer.hasNext(), is(true));
    assertThat(tokenizer.cursor(), is(7));
  }

  @Test
  public void tokenCandidates() {
    DocumentTokenizer tokenizer = tokenizer("-12.34 hello");

    assertThat(tokenizer.hasNext(), is(true));
    assertThat(tokenizer.cursor(), is(0));

    Set<Token> candidates = tokenizer.candidates();

    assertThat(candidates, is(not(empty())));
    assertThat(candidates, hasSize(3));

    assertThat(
        candidates,
        containsInAnyOrder(
            Token.create(CommonTokenTypes.OPERATOR, "-"),
            Token.create(CommonTokenTypes.SIGNED_INT, "-12"),
            Token.create(CommonTokenTypes.SIGNED_FLOAT, "-12.34")));

    assertThat(tokenizer.hasNext(), is(true));
    assertThat(tokenizer.cursor(), is(0));
  }

  @Test
  public void rewindingToken() {
    DocumentTokenizer tokenizer = tokenizer("1234");

    Token token = tokenizer.next();
    assertThat(token, is(notNullValue()));

    tokenizer.rewind(token);

    assertThat(tokenizer.cursor(), is(0));
  }

  @Test
  public void streamingTokens() {
    List<Token> tokens = tokenizer("a 12 b").stream().collect(toList());

    assertThat(tokens, is(notNullValue()));
    assertThat(
        tokens,
        contains(
            Token.create(CommonTokenTypes.CONSTANT, "a"),
            Token.create(CommonTokenTypes.WHITESPACE, " "),
            Token.create(CommonTokenTypes.UNSIGNED_INT, "12"),
            Token.create(CommonTokenTypes.WHITESPACE, " "),
            Token.create(CommonTokenTypes.CONSTANT, "b")));
  }
}
