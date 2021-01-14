package com.mmnaseri.utils.dareader;

/** Accessor that can navigate the internal contents of a structured document. */
public interface DocumentAccessor {

  /** Returns the current line number. The first line has a line number of {@code 1}. */
  int line();

  /**
   * Returns the current character number in the current line. The first character in the line has a
   * number of {@code 1}.
   */
  int offset();

  /** Returns the number of characters read so far from the beginning of the document. */
  int cursor();

  /** Indicates if there are more text left in the wrapped document to be read. */
  boolean hasNext();

  /** Returns the snapshot manager for this document. */
  DocumentSnapshotManager snapshot();
}
