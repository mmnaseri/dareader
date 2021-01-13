package com.mmnaseri.utils.dareader;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Wraps a document and tokenizes its contents. The cursor in the document can move forward and
 * backward, so this is not really a suitable candidate for parsing extremely large documents, since
 * any implementation must support arbitrarily moving back and forth in the document.
 */
public interface DocumentTokenizer extends Iterator<Token> {

  /**
   * Indicates if there are more text left in the wrapped document to be read. Note that once this
   * method returns {@code false} it is guaranteed that {@link #next()} will throw an exception.
   * However, if this method returns {@code true}, there might be more <em>text</em> but no more
   * recognizable tokens in the remainder of the document.
   */
  @Override
  boolean hasNext();

  /** Reads the next token from the document. */
  @Override
  Token next();

  /** Returns the current line number. The first line has a line number of {@code 1}. */
  int line();

  /**
   * Returns the current character number in the current line. The first character in the line has a
   * number of {@code 1}.
   */
  int offset();

  /** Returns the number of characters read so far from the beginning of the document. */
  int cursor();

  /**
   * Rewinds the tokenizer by putting the token back at the top of the stack. This means that the
   * identity {@code next().rewind().next() == next()} should always hold true.
   *
   * <p>Returns a pointer to the same instance of the tokenizer for convenience.
   */
  DocumentTokenizer rewind(Token token);

  /** Returns a spliterator for this document. */
  default Spliterator<Token> spliterator() {
    return Spliterators.spliteratorUnknownSize(
        /* iterator= */ this, Spliterator.IMMUTABLE | Spliterator.NONNULL);
  }

  /** Returns a stream from the tokens of this document. */
  default Stream<Token> stream() {
    return StreamSupport.stream(spliterator(), /* parallel= */ false);
  }
}
