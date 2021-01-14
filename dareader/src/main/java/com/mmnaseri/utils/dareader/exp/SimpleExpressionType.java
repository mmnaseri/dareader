package com.mmnaseri.utils.dareader.exp;

import java.util.Objects;

/** A default expression type which represents a composition. */
public class SimpleExpressionType implements ExpressionType {

  private final int tag;
  private final String name;

  SimpleExpressionType(int tag, String name) {
    this.tag = tag;
    this.name = name;
  }

  @Override
  public int tag() {
    return tag;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(tag());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ExpressionType)) {
      return false;
    }
    return ((ExpressionType) obj).tag() == tag();
  }
}
