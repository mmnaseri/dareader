package com.mmnaseri.utils.dareader;

import javax.annotation.Nonnull;

/** A simple token read from the input. */
public interface Token {

  /** The type of token this token is. */
  @Nonnull
  TokenType type();

  /** The value of this token. */
  @Nonnull
  String value();

  /**
   * The length of this token as read originally from the document. This might not be the same as
   * the length of the content represented in {@link #value()}.
   */
  int length();
}
