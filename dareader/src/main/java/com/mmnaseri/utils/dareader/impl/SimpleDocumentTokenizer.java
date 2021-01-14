package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.DocumentReader;
import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.dareader.DocumentSnapshotManager;
import com.mmnaseri.utils.dareader.DocumentTokenizer;
import com.mmnaseri.utils.dareader.token.Token;
import com.mmnaseri.utils.dareader.token.TokenReader;
import com.mmnaseri.utils.dareader.token.TokenType;
import com.mmnaseri.utils.dareader.token.TokenTypeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;
import static java.util.stream.Collectors.toList;

/** A simple document tokenizer. */
public class SimpleDocumentTokenizer implements DocumentTokenizer {

  private final TokenTypeRegistry registry;
  private final DocumentReader reader;

  public SimpleDocumentTokenizer(TokenTypeRegistry registry, String document) {
    this.registry = registry;
    reader = DocumentReader.of(document);
  }

  @Override
  public int line() {
    return reader.line();
  }

  @Override
  public int offset() {
    return reader.offset();
  }

  @Override
  public int cursor() {
    return reader.cursor();
  }

  @Override
  public boolean hasNext() {
    return reader.hasNext();
  }

  @Override
  public DocumentSnapshotManager snapshot() {
    return reader.snapshot();
  }

  @Override
  @Nullable
  public Token next() {
    List<TokenReader> readers =
        registry.tokenTypes().stream()
            .sorted(Comparator.comparing(TokenType::tag))
            .map(registry::reader)
            .collect(toList());
    DocumentSnapshot snapshot = snapshot().create();
    for (TokenReader tokenReader : readers) {
      Token token = tokenReader.read(reader);
      if (token == null) {
        snapshot().restore(snapshot);
      } else {
        return token;
      }
    }
    return null;
  }

  @Nonnull
  @Override
  public Set<Token> candidates() {
    Set<TokenType> types = registry.tokenTypes();
    DocumentSnapshot snapshot = snapshot().create();
    Set<Token> candidates = new HashSet<>();
    for (TokenType type : types) {
      TokenReader reader = registry.reader(type);
      Token token = reader.read(this.reader);
      if (token != null) {
        candidates.add(token);
      }
      snapshot().restore(snapshot);
    }
    return candidates;
  }

  @Override
  public DocumentTokenizer rewind(Token token) {
    checkNotNull(token, "token cannot be null");
    reader.rewind(token.length());
    return this;
  }
}
