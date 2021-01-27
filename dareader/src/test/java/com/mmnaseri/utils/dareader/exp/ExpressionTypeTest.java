package com.mmnaseri.utils.dareader.exp;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ExpressionTypeTest {

  @Test
  public void testEmpty() {
    assertThat(ExpressionType.EMPTY, is(not(new Object())));
    assertThat(ExpressionType.EMPTY, is(ExpressionType.EMPTY));
    assertThat(ExpressionType.EMPTY.tag(), is(ExpressionType.EMPTY.tag()));
    assertThat(ExpressionType.EMPTY.hashCode(), is(ExpressionType.EMPTY.hashCode()));
    assertThat(ExpressionType.EMPTY.toString(), is("EMPTY"));
  }

  @Test
  public void testComposite() {
    assertThat(ExpressionType.COMPOSITE, is(not(new Object())));
    assertThat(ExpressionType.COMPOSITE, is(ExpressionType.COMPOSITE));
    assertThat(ExpressionType.COMPOSITE.tag(), is(ExpressionType.COMPOSITE.tag()));
    assertThat(ExpressionType.COMPOSITE.hashCode(), is(ExpressionType.COMPOSITE.hashCode()));
    assertThat(ExpressionType.COMPOSITE.toString(), is("COMPOSITE"));
  }

  @Test
  public void testOptional() {
    assertThat(ExpressionType.OPTIONAL, is(not(new Object())));
    assertThat(ExpressionType.OPTIONAL, is(ExpressionType.OPTIONAL));
    assertThat(ExpressionType.OPTIONAL.tag(), is(ExpressionType.OPTIONAL.tag()));
    assertThat(ExpressionType.OPTIONAL.hashCode(), is(ExpressionType.OPTIONAL.hashCode()));
    assertThat(ExpressionType.OPTIONAL.toString(), is("OPTIONAL"));
  }
}