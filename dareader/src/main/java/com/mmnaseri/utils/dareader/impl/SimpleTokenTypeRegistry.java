package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.TokenType;
import com.mmnaseri.utils.dareader.TokenTypeRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkState;

/** A simple implementation for {@link TokenTypeRegistry}. */
public class SimpleTokenTypeRegistry implements TokenTypeRegistry {

  private final Map<Integer, TokenType> types;

  public SimpleTokenTypeRegistry() {
    this(Collections.emptyMap());
  }

  public SimpleTokenTypeRegistry(Map<Integer, TokenType> types) {
    this.types = new HashMap<>(types);
  }

  @Override
  public void add(final TokenType tokenType) {
    checkNotNull(tokenType, "Token type cannot be null");
    checkState(
        !types.containsKey(tokenType.tag()),
        "Another token type (%s) already exists with this tag (%d)",
        types.get(tokenType.tag()),
        tokenType.tag());
    types.put(tokenType.tag(), tokenType);
  }

  @Override
  public Set<TokenType> tokenTypes() {
    return new HashSet<>(types.values());
  }

  @Override
  public TokenTypeRegistry copy() {
    return new SimpleTokenTypeRegistry(types);
  }

  @Override
  public TokenTypeRegistry merge(final TokenTypeRegistry other) {
    final TokenTypeRegistry copy = copy();
    other.tokenTypes().forEach(copy::add);
    return copy;
  }
}
