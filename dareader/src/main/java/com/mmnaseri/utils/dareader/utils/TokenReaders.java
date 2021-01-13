package com.mmnaseri.utils.dareader.utils;

import com.mmnaseri.utils.dareader.TokenReader;
import com.mmnaseri.utils.dareader.TokenType;

import java.util.regex.Pattern;

/** A container for the most common token readers. */
public final class TokenReaders {

  private TokenReaders() throws IllegalAccessException {
    throw new IllegalAccessException("This type should not be instantiated.");
  }

  public static TokenReader pattern(TokenType type, String pattern) {
    return pattern(type, Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE));
  }

  public static TokenReader pattern(TokenType type, Pattern pattern) {
    return new PatternTokenReader(type, pattern);
  }

  public static TokenReader pattern(TokenType type, String pattern, int group) {
    return pattern(type, Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE), group);
  }

  public static TokenReader pattern(TokenType type, Pattern pattern, int group) {
    return new PatternTokenReader(type, pattern, group);
  }
}
