package com.mmnaseri.utils.dareader;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkState;

/** A snapshot that represents the state of a document reader in a given time. */
public interface DocumentSnapshot {

  /** Returns the current line number. The first line has a line number of {@code 1}. */
  int line();

  /**
   * Returns the current character number in the current line. The first character in the line has a
   * number of {@code 1}.
   */
  int offset();

  /** Returns the number of characters read so far from the beginning of the document. */
  int cursor();

  /** Returns {@code true} if there is any more text in the document to be read. */
  boolean hasNext();

  /**
   * Returns the number of characters the document reader has moved since this snapshot was created.
   */
  default int distance(DocumentReader reader) {
    checkNotNull(reader, "Reader cannot be null");
    checkState(
        reader.snapshot().knows(this), "This snapshot does not belong to the indicated document");
    return reader.cursor() - cursor();
  }
}
