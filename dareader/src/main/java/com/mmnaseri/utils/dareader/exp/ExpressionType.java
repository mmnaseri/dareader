package com.mmnaseri.utils.dareader.exp;

/**
 * Represents the type of an expression node. It is important to make sure that instances of this
 * type implement the {@link #hashCode()} and {@link #equals(Object)} methods correctly. Instances
 * are supposed to be singletons.
 */
public interface ExpressionType {

  int BASE_TAG = 1_000_000_000;
  ExpressionType EMPTY = of(BASE_TAG + 1, "EMPTY");
  ExpressionType COMPOSITE = of(BASE_TAG + 2, "COMPOSITE");
  ExpressionType OPTIONAL = of(BASE_TAG + 3, "OPTIONAL");

  static ExpressionType of(int tag, String name) {
    return new SimpleExpressionType(tag, name);
  }

  /**
   * The expression tag for the current type. This is supposed to be unique per type for any given
   * grammar.
   */
  int tag();
}
