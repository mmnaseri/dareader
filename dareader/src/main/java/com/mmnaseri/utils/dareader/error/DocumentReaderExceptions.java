package com.mmnaseri.utils.dareader.error;

import com.mmnaseri.utils.dareader.DocumentTokenizer;

/** Utility class for checking pre- and post-conditions when processing a document. */
public final class DocumentReaderExceptions {

  private DocumentReaderExceptions() throws IllegalAccessException {
    throw new IllegalAccessException("This type should not be instantiated.");
  }

  public static <E> E expectToken(DocumentTokenizer tokenizer, E value) {
    if (value == null) {
      throw new DocumentReaderException(
          tokenizer, "Expected to read a value, but nothing was matched.");
    }
    return value;
  }
}
