package com.mmnaseri.utils.dareader.error;

import com.mmnaseri.utils.dareader.DocumentAccessor;

/** The base class for the exceptions occurring when reading the contents of a document. */
public class DocumentReaderException extends RuntimeException {

  private final int line;
  private final int offset;

  public DocumentReaderException(DocumentAccessor accessor, String message) {
    this(accessor.line(), accessor.offset(), message);
  }

  public DocumentReaderException(int line, int offset, String message) {
    super(message);
    this.line = line;
    this.offset = offset;
  }

  /** The line at which the error was encountered. */
  public int line() {
    return line;
  }

  /** The character number in the line at which the error happened. */
  public int offset() {
    return offset;
  }
}
