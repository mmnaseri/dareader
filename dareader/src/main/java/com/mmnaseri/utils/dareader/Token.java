package com.mmnaseri.utils.dareader;

/** A simple token read from the input. */
public interface Token {

  /** The type of token this token is. */
  TokenType type();

  /** The value of this token. */
  String value();

  /**
   * The length of this token as read originally from the document. This might not be the same as
   * the length of the content represented in {@link #value()}.
   */
  int length();
}
