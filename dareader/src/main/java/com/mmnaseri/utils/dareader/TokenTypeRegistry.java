package com.mmnaseri.utils.dareader;

import java.util.Set;

/**
 * A registry which can be used to look up token types by their identifier. This ensures that all
 * expected tokens in a given context are well-defined. Moreover, we can now ensure that all tokens
 * act as fly-wheels for the parsing mechanism.
 */
public interface TokenTypeRegistry {

  /** Adds a new token and its corresponding reader to the registry. */
  void add(TokenType tokenType, TokenReader reader);

  /** Returns the token reader for the specified token type. */
  TokenReader reader(TokenType tokenType);

  /** Returns all known token types. */
  Set<TokenType> tokenTypes();

  /**
   * Returns a copy of the current registry that can be used to add new token types while keeping
   * this instance intact. This is useful when we have several common token types, but the specifics
   * of the context force us to use different types for different purposes.
   */
  TokenTypeRegistry copy();

  /**
   * Returns a copy of the same registry using {@link #copy()} and adds all the tokens registered in
   * the other registry to it.
   */
  TokenTypeRegistry merge(TokenTypeRegistry other);
}
