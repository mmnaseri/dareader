package com.mmnaseri.utils.dareader.token;

import com.mmnaseri.utils.dareader.token.impl.SimpleToken;

import javax.annotation.Nonnull;

/** A simple token read from the input. */
public interface Token {

  /**
   * Creates a new token assuming that the length of the token and the length of the value are the
   * same.
   */
  static Token create(TokenType type, String value) {
    return create(type, value, value.length());
  }

  /** Creates a new token with the given parameters. */
  static Token create(TokenType type, String value, int length) {
    return new SimpleToken(type, value, length);
  }

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
