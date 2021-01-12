package com.mmnaseri.utils.dareader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static com.mmnaseri.utils.dareader.error.DocumentReaderExceptions.expectToken;

/**
 * A document reader will wrap a string document and expose convenience methods to traverse it in a
 * structured way. The methods on this are meant for the most part to be used by {@link TokenReader}
 * instances which can be used to deliver tokens out of the original document.
 */
public interface DocumentReader extends DocumentTokenizer, CharSequence {

  /**
   * Reads a single character from the document. Will throw an exception if the document has no more
   * tokens.
   */
  char read();

  /**
   * Reads the output that matches the given pattern, and returns the contents the matched pattern.
   * The pattern <em>must</em> match content starting at the current {@link #cursor()}.
   *
   * <p>A return value of {@code null} means that no matches were found and therefore the cursor
   * hasn't moved.
   *
   * @see #read(Pattern, int) this method calls {@link #read(Pattern, int)} with the group set to
   *     {@code 0}.
   */
  @Nullable
  default String read(Pattern pattern) {
    return read(pattern, 0);
  }

  /**
   * Reads the output that matches the given pattern, and returns the contents of the indicated
   * group, with group {@code 0} meaning the entirety of the matched pattern. The pattern
   * <em>must</em> match content starting at the current {@link #cursor()}.
   *
   * <p>A return value of {@code null} might mean that no matches were found and therefore the
   * cursor hasn't moved, or that the indicated group was not matched, but the pattern <em>did</em>
   * match.
   */
  @Nullable
  String read(Pattern pattern, int group);

  /** Same as {@link #expect(Pattern, int)} but with the group set to {@code 0}. */
  @Nonnull
  default String expect(Pattern pattern) {
    return expect(pattern, 0);
  }

  /**
   * The same as {@link #read(Pattern, int)}. The only difference is that if the returned value is
   * {@code null}, it will throw an exception.
   */
  @Nonnull
  default String expect(Pattern pattern, int group) {
    return expectToken(this, read(pattern, group));
  }

  /** Rewinds the document to the indicated number of tokens. */
  DocumentReader rewind(int length);
}
