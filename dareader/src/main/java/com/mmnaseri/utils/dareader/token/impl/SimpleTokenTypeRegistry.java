package com.mmnaseri.utils.dareader.token.impl;

import com.mmnaseri.utils.dareader.token.TokenReader;
import com.mmnaseri.utils.dareader.token.TokenType;
import com.mmnaseri.utils.dareader.token.TokenTypeRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkState;
import static java.util.stream.Collectors.toSet;

/** A simple implementation for {@link TokenTypeRegistry}. */
public class SimpleTokenTypeRegistry implements TokenTypeRegistry {

  private final Map<Integer, TokenDefinition> types;

  public SimpleTokenTypeRegistry() {
    this(Collections.emptyMap());
  }

  public SimpleTokenTypeRegistry(Map<Integer, TokenDefinition> types) {
    this.types = new HashMap<>(types);
  }

  @Override
  public void add(TokenType tokenType, TokenReader reader) {
    checkNotNull(tokenType, "Token type cannot be null");
    checkState(
        !types.containsKey(tokenType.tag()),
        "Another token type (%s) already exists with this tag (%d)",
        types.get(tokenType.tag()),
        tokenType.tag());
    types.put(tokenType.tag(), new TokenDefinition(tokenType, reader));
  }

  @Override
  public TokenReader reader(TokenType tokenType) {
    checkNotNull(tokenType, "Token type cannot be null");
    checkState(types.containsKey(tokenType.tag()), "No token reader for type %s exists", tokenType);
    return types.get(tokenType.tag()).reader();
  }

  @Override
  public Set<TokenType> tokenTypes() {
    return types.values().stream().map(TokenDefinition::type).collect(toSet());
  }

  @Override
  public TokenTypeRegistry copy() {
    return new SimpleTokenTypeRegistry(types);
  }

  @Override
  public TokenTypeRegistry merge(TokenTypeRegistry other) {
    final TokenTypeRegistry copy = copy();
    other.tokenTypes().forEach(tokenType -> copy.add(tokenType, reader(tokenType)));
    return copy;
  }

  /** Simple class used to store details of a token type and its associated reader. */
  private static class TokenDefinition {
    private final TokenType type;
    private final TokenReader reader;

    private TokenDefinition(TokenType type, TokenReader reader) {
      this.type = type;
      this.reader = reader;
    }

    private TokenType type() {
      return type;
    }

    private TokenReader reader() {
      return reader;
    }
  }
}
