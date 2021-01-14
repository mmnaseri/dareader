package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.DocumentReader;
import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.dareader.DocumentSnapshotManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mmnaseri.utils.dareader.error.DocumentReaderExceptions.expectDistance;
import static com.mmnaseri.utils.dareader.error.DocumentReaderExceptions.expectMore;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkState;

/** A simple implementation for {@link DocumentReader}. */
public class SimpleDocumentReader implements DocumentReader {

  private final String document;
  private final SnapshotManager snapshotManager;
  private int cursor;
  private int line;
  private int offset;

  public SimpleDocumentReader(String document) {
    this.document = document;
    snapshotManager = new SnapshotManager();
    cursor = 0;
    line = 1;
    offset = 1;
  }

  private void move(char character) {
    if (character == '\n') {
      line++;
      offset = 1;
    } else {
      offset++;
    }
    snapshotManager.snapshot = null;
  }

  @Override
  public char read() {
    expectMore(this);
    char character = charAt(cursor++);
    move(character);
    return character;
  }

  @Nullable
  @Override
  public String read(Pattern pattern, int group) {
    expectMore(this);
    Matcher matcher = pattern.matcher(this);
    if (!matcher.find(cursor) || matcher.start() != cursor) {
      return null;
    }
    String matched = matcher.group(0);
    for (int i = 0; i < matched.length(); i++) {
      move(matched.charAt(i));
    }
    return matcher.group(group);
  }

  @Override
  public DocumentReader rewind(int length) {
    expectDistance(this, length);
    offset = 0;
    for (int i = 0; i < length; i++) {
      cursor--;
      if (charAt(cursor) == '\n') {
        line--;
      }
    }
    int seek = cursor;
    offset = 0;
    while (seek > 0 && charAt(seek) != '\n') {
      offset++;
    }
    if (seek == 0) {
      line = 1;
    }
    return this;
  }

  @Override
  public DocumentSnapshotManager snapshot() {
    return snapshotManager;
  }

  @Override
  public boolean hasNext() {
    return cursor < length();
  }

  @Override
  public int line() {
    return line;
  }

  @Override
  public int offset() {
    return offset;
  }

  @Override
  public int cursor() {
    return cursor;
  }

  @Override
  public int length() {
    return document.length();
  }

  @Override
  public char charAt(int index) {
    return document.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return document.subSequence(start, end);
  }

  @Override
  @Nonnull
  public String toString() {
    return subSequence(0, cursor) + "^" + subSequence(cursor, length());
  }

  private class SnapshotManager implements DocumentSnapshotManager {

    private Snapshot snapshot;

    @Override
    public DocumentSnapshot create() {
      if (snapshot == null) {
        snapshot = new Snapshot(line(), offset(), cursor(), hasNext());
      }
      return snapshot;
    }

    @Override
    public DocumentReader restore(DocumentSnapshot snapshot) {
      checkState(knows(snapshot), "The provided snapshot does not belong to this document.");
      cursor = snapshot.cursor();
      line = snapshot.line();
      offset = snapshot.offset();
      return SimpleDocumentReader.this;
    }

    @Override
    public boolean knows(DocumentSnapshot snapshot) {
      return snapshot instanceof Snapshot && ((Snapshot) snapshot).belongsTo(this);
    }

    private class Snapshot implements DocumentSnapshot {

      private final int line;
      private final int offset;
      private final int cursor;
      private final boolean hasNext;

      private Snapshot(int line, int offset, int cursor, boolean hasNext) {
        this.line = line;
        this.offset = offset;
        this.cursor = cursor;
        this.hasNext = hasNext;
      }

      @Override
      public int line() {
        return line;
      }

      @Override
      public int offset() {
        return offset;
      }

      @Override
      public int cursor() {
        return cursor;
      }

      @Override
      public boolean hasNext() {
        return hasNext;
      }

      public boolean belongsTo(SnapshotManager manager) {
        return manager == SnapshotManager.this;
      }
    }
  }
}
