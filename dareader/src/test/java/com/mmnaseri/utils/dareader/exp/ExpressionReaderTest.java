package com.mmnaseri.utils.dareader.exp;

import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.dareader.DocumentTokenizer;
import com.mmnaseri.utils.dareader.impl.SimpleDocumentTokenizer;
import com.mmnaseri.utils.dareader.token.Token;
import com.mmnaseri.utils.dareader.token.TokenReader;
import com.mmnaseri.utils.dareader.token.TokenType;
import com.mmnaseri.utils.dareader.token.TokenTypeRegistry;
import com.mmnaseri.utils.dareader.token.impl.SimpleTokenTypeRegistry;
import com.mmnaseri.utils.dareader.utils.TokenReaders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/** Tests for {@link ExpressionReader}. */
public class ExpressionReaderTest {

  private ExpressionReader evenReader;
  private ExpressionReader oddReader;

  @BeforeMethod
  public void setUp() {
    evenReader = new StaticExpressionReader(value -> value % 2 == 0);
    oddReader = new StaticExpressionReader(value -> value % 2 == 1);
  }

  @Test
  public void testSimpleExpression() {
    assertThat(
        new StaticExpressionReader(value -> true).read(tokenizer("abcde")),
        is(StatTokens.STATIC_TOKEN_A.expression()));

    // Even reader should work for evens.
    checkNotMatch(evenReader, "b");
    assertThat(evenReader.read(tokenizer("c")), is(StatTokens.STATIC_TOKEN_C.expression()));

    // Odd reader should work for odds.
    checkNotMatch(oddReader, "c");
    assertThat(oddReader.read(tokenizer("b")), is(StatTokens.STATIC_TOKEN_B.expression()));
  }

  @Test
  public void testDisjunctionMatching() {
    // Disjunction should work for all.
    assertThat(
        evenReader.or(oddReader).read(tokenizer("b")), is(StatTokens.STATIC_TOKEN_B.expression()));
    assertThat(
        evenReader.or(oddReader).read(tokenizer("c")), is(StatTokens.STATIC_TOKEN_C.expression()));
    assertThat(
        oddReader.or(evenReader).read(tokenizer("b")), is(StatTokens.STATIC_TOKEN_B.expression()));
    assertThat(
        oddReader.or(evenReader).read(tokenizer("c")), is(StatTokens.STATIC_TOKEN_C.expression()));
  }

  @Test
  public void testDisjunctionNotMatching() {
    StaticExpressionReader first = new StaticExpressionReader(value -> value >= 2);
    StaticExpressionReader second = new StaticExpressionReader(value -> value >= 3);
    ExpressionReader reader = first.or(second);

    assertThat(reader.read(tokenizer("cd")), is(StatTokens.STATIC_TOKEN_C.expression()));
    checkNotMatch(reader, "ab");
  }

  @Test
  public void testConjunctionMatching() {
    assertThat(
        evenReader.and(oddReader).read(tokenizer("ab")),
        is(
            Expression.compose(
                StatTokens.STATIC_TOKEN_A.expression(), StatTokens.STATIC_TOKEN_B.expression())));

    assertThat(
        evenReader.and(oddReader).and(evenReader).read(tokenizer("abc")),
        is(
            Expression.compose(
                StatTokens.STATIC_TOKEN_A.expression(),
                StatTokens.STATIC_TOKEN_B.expression(),
                StatTokens.STATIC_TOKEN_C.expression())));

    assertThat(
        evenReader.and(oddReader.and(evenReader)).read(tokenizer("abc")),
        is(
            Expression.compose(
                StatTokens.STATIC_TOKEN_A.expression(),
                StatTokens.STATIC_TOKEN_B.expression(),
                StatTokens.STATIC_TOKEN_C.expression())));
  }

  @Test
  public void testConjunctionNotMatching() {
    checkNotMatch(evenReader.and(evenReader).and(oddReader), "aaa");
    checkNotMatch(evenReader.and(evenReader).and(oddReader), "bab");
  }

  @Test
  public void testNegationMatching() {
    assertThat(evenReader.negate().read(tokenizer("b")), is(Expression.empty()));
  }

  @Test
  public void testNegationNotMatching() {
    checkNotMatch(evenReader.negate(), "c");
  }

  @Test
  public void testOptionalMatching() {
    assertThat(
        evenReader.optional().read(tokenizer("a")),
        is(Expression.optional(StatTokens.STATIC_TOKEN_A.expression())));
  }

  @Test
  public void testOptionalNotMatching() {
    Expression expression = evenReader.optional().read(tokenizer("b"));

    assertThat(expression, is(notNullValue()));
    assertThat(expression, is(Expression.optional()));
  }

  @Test
  public void testTimesMatching() {
    assertThat(
        evenReader.times(2).read(tokenizer("acebb")),
        is(
            Expression.compose(
                StatTokens.STATIC_TOKEN_A.expression(), StatTokens.STATIC_TOKEN_C.expression())));
  }

  @Test
  public void testTimesNotMatching() {
    checkNotMatch(evenReader.times(4), "acebb");
  }

  @Test
  public void testRepeatedMatching() {
    assertThat(
        evenReader.repeated().read(tokenizer("acebb")),
        is(
            Expression.compose(
                StatTokens.STATIC_TOKEN_A.expression(),
                StatTokens.STATIC_TOKEN_C.expression(),
                StatTokens.STATIC_TOKEN_E.expression())));
  }

  @Test
  public void testRepeatedNotMatching() {
    assertThat(evenReader.repeated().read(tokenizer("bacebb")), is(Expression.empty()));
  }

  @Test
  public void testAtLeastOnceMatching() {
    assertThat(
        evenReader.atLeastOnce().read(tokenizer("acebb")),
        is(
            Expression.compose(
                StatTokens.STATIC_TOKEN_A.expression(),
                StatTokens.STATIC_TOKEN_C.expression(),
                StatTokens.STATIC_TOKEN_E.expression())));
    assertThat(
        evenReader.atLeastOnce().read(tokenizer("abcebb")),
        is(Expression.compose(StatTokens.STATIC_TOKEN_A.expression())));
  }

  @Test
  public void testAtLeastOnceNotMatching() {
    checkNotMatch(evenReader.atLeastOnce(), "xacebb");
  }

  private void checkNotMatch(ExpressionReader reader, String document) {
    DocumentTokenizer tokenizer = tokenizer(document);
    int cursor = tokenizer.cursor();

    Expression expression = reader.read(tokenizer);
    assertThat(expression, is(nullValue()));
    assertThat(tokenizer.cursor(), is(cursor));
  }

  private static DocumentTokenizer tokenizer(String document) {
    return new SimpleDocumentTokenizer(StatTokens.registry(), document);
  }

  private enum StatTokens implements TokenType {
    STATIC_TOKEN_A,
    STATIC_TOKEN_B,
    STATIC_TOKEN_C,
    STATIC_TOKEN_D,
    STATIC_TOKEN_E,
    ;

    private Token token() {
      return Token.create(this, value());
    }

    private Expression expression() {
      return StatExpressions.STATIC_EXPRESSION.create(token());
    }

    @Override
    public int tag() {
      return ordinal();
    }

    private String value() {
      return name().toLowerCase(Locale.ROOT).substring("STATIC_TOKEN_".length());
    }

    private TokenReader reader() {
      return TokenReaders.pattern(this, Pattern.quote(value()));
    }

    private static TokenTypeRegistry registry() {
      SimpleTokenTypeRegistry registry = new SimpleTokenTypeRegistry();
      for (StatTokens type : values()) {
        registry.add(type, type.reader());
      }
      return registry;
    }
  }

  private enum StatExpressions implements ExpressionType {
    STATIC_EXPRESSION;

    private Expression create(Token... tokens) {
      return Expression.newBuilder(this).addAllTokens(Arrays.asList(tokens)).build();
    }

    @Override
    public int tag() {
      return ordinal();
    }
  }

  private static final class StaticExpressionReader implements ExpressionReader {

    private final IntPredicate tagPredicate;

    private StaticExpressionReader(IntPredicate tagPredicate) {
      this.tagPredicate = tagPredicate;
    }

    @Nullable
    @Override
    public Expression read(DocumentTokenizer tokenizer) {
      DocumentSnapshot snapshot = tokenizer.snapshot().create();
      Token token = tokenizer.next();
      if (token == null || !tagPredicate.test(token.type().tag())) {
        snapshot.apply();
        return null;
      }
      return Expression.newBuilder(StatExpressions.STATIC_EXPRESSION).addToken(token).build();
    }
  }
}
