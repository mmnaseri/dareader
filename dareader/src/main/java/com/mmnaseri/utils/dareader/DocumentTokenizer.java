package com.mmnaseri.utils.dareader;

import com.mmnaseri.utils.dareader.impl.SimpleDocumentTokenizer;
import com.mmnaseri.utils.dareader.token.Token;
import com.mmnaseri.utils.dareader.token.TokenTypeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Wraps a document and tokenizes its contents. The cursor in the document can move forward and
 * backward, so this is not really a suitable candidate for parsing extremely large documents, since
 * any implementation must support arbitrarily moving back and forth in the document.
 */
public interface DocumentTokenizer extends DocumentAccessor, Iterator<Token> {

  /** Creates a factory that binds the token types to the presented set. */
  static DocumentTokenizerFactory with(TokenTypeRegistry registry) {
    return document -> new SimpleDocumentTokenizer(registry, document);
  }

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
  @Nullable
  Token next();

  /** Returns a set of tokens that could be read from this point in the document. */
  @Nonnull
  Set<Token> candidates();

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
