package com.mmnaseri.utils.dareader.error;

import com.mmnaseri.utils.dareader.DocumentAccessor;

import java.text.MessageFormat;

/** Utility class for checking pre- and post-conditions when processing a document. */
public final class DocumentReaderExceptions {

  private DocumentReaderExceptions() throws IllegalAccessException {
    throw new IllegalAccessException("This type should not be instantiated.");
  }

  public static <E> E expectValue(DocumentAccessor accessor, E value) {
    if (value == null) {
      throw new DocumentReaderException(
          accessor, "Expected to read a value, but nothing was matched.");
    }
    return value;
  }

  public static void expectMore(DocumentAccessor accessor) {
    if (!accessor.hasNext()) {
      throw new DocumentReaderException(accessor, "Expected to see more in the document");
    }
  }

  public static void expectDistance(DocumentAccessor accessor, int expected) {
    if (accessor.cursor() < expected) {
      throw new DocumentReaderException(
          accessor,
          MessageFormat.format(
              "Expected to have read at least {0} characters from the document", expected));
    }
  }
}
