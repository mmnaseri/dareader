package com.mmnaseri.utils.dareader.utils;

import com.mmnaseri.utils.dareader.DocumentReader;
import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.dareader.token.Token;
import com.mmnaseri.utils.dareader.token.TokenReader;
import com.mmnaseri.utils.dareader.token.TokenType;
import com.mmnaseri.utils.dareader.token.impl.SimpleToken;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

/** Simple token reader that can read any token that matches a certain pattern. */
public class PatternTokenReader implements TokenReader {

  private final TokenType tokenType;
  private final Pattern pattern;
  private final int group;

  PatternTokenReader(TokenType tokenType, Pattern pattern) {
    this(tokenType, pattern, 0);
  }

  PatternTokenReader(TokenType tokenType, Pattern pattern, int group) {
    this.pattern = pattern;
    this.group = group;
    this.tokenType = tokenType;
  }

  @Nullable
  @Override
  public Token read(DocumentReader reader) {
    DocumentSnapshot snapshot = reader.snapshot().create();
    final String value = reader.read(pattern, group);
    if (value == null) {
      reader.snapshot().restore(snapshot);
      return null;
    }
    return new SimpleToken(tokenType, value, snapshot.distance(reader));
  }
}
