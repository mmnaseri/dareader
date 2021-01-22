package com.mmnaseri.utils.dareader.exp;

import com.mmnaseri.utils.dareader.token.Token;
import com.mmnaseri.utils.dareader.token.impl.CommonTokenTypes;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ExpressionTest {

  @Test
  public void expressionEquality() {
    ExpressionType type = ExpressionType.of(1, "TYPE");
    Expression first =
        Expression.newBuilder(type)
            .addToken(Token.create(CommonTokenTypes.CONSTANT, "true"))
            .build();
    Expression second =
        Expression.newBuilder(type)
            .addToken(Token.create(CommonTokenTypes.CONSTANT, "true"))
            .build();
    assertThat(first, is(second));
  }

  @Test
  public void parentChildRelationship() {
    ExpressionType type1 = ExpressionType.of(1, "TYPE_1");
    ExpressionType type2 = ExpressionType.of(2, "TYPE_2");
    ExpressionType type3 = ExpressionType.of(3, "TYPE_3");

    Expression first = Expression.newBuilder(type1).build();
    Expression second = Expression.newBuilder(type2).build();
    Expression third = Expression.newBuilder(type3).build();

    first = first.withChild(second);
    first = first.withChild(third);

    assertThat(second.parent(), is(notNullValue()));
    assertThat(second.parentNonNull(), is(first));

    assertThat(third.parent(), is(notNullValue()));
    assertThat(third.parentNonNull(), is(first));

    assertThat(first.children(), contains(second, third));
  }
}
