package com.mmnaseri.utils.dareader;

import javax.annotation.Nullable;

/** Reads a token from the input. */
public interface TokenReader {

  /**
   * Reads a token from the document. The idea is that this particular token reader is only
   * responsible for producing tokens that belong to a specific set of {@link TokenType}s.
   *
   * <p>If the document does not contain any tokens that can be recognized by this reader, then the
   * reader will return {@code null}.
   */
  @Nullable
  Token read(DocumentReader reader);

  /**
   * Returns a reader that first attempts to read the token designated by the current reader and
   * then the one from the other reader. If either of these two returns {@code null}, the document
   * is restored and the composed reader will return {@code null}.
   *
   * <p>If both readers return a token, the last token is returned.
   */
  default TokenReader then(TokenReader other) {
    return reader -> {
      DocumentSnapshot snapshot = reader.snapshot().create();
      Token first = read(reader);
      if (first == null) {
        reader.snapshot().restore(snapshot);
        return null;
      }
      Token second = other.read(reader);
      if (second == null) {
        reader.snapshot().restore(snapshot);
        return null;
      }
      return second;
    };
  }
}
