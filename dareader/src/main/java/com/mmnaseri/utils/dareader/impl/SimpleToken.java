package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.Token;
import com.mmnaseri.utils.dareader.TokenType;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkArgument;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;

/** Simple immutable token class. */
public class SimpleToken implements Token {

  private final TokenType type;
  private final String value;
  private final int length;

  public SimpleToken(TokenType type, String value, int length) {
    this.type = checkNotNull(type, "Type cannot be null");
    this.value = checkNotNull(value, "Value cannot be null");
    checkArgument(length > 0, "Token length should be positive");
    this.length = length;
  }

  @Nonnull
  @Override
  public TokenType type() {
    return type;
  }

  @Nonnull
  @Override
  public String value() {
    return value;
  }

  @Override
  public int length() {
    return length;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Token)) {
      return false;
    }
    Token that = (Token) o;
    return length() == that.length() && type().equals(that.type()) && value().equals(that.value());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), value(), length());
  }

  @Override
  public String toString() {
    return "Token{type=" + type() + ",value=" + value() + ",length=" + length() + "}";
  }
}
