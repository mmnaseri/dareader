package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.DocumentReader;
import com.mmnaseri.utils.dareader.DocumentSnapshotManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mmnaseri.utils.dareader.error.DocumentReaderExceptions.expectDistance;
import static com.mmnaseri.utils.dareader.error.DocumentReaderExceptions.expectMore;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkArgument;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;

/** A simple implementation for {@link DocumentReader}. */
public class SimpleDocumentReader implements DocumentReader {

  private final String document;
  private final ReferenceBasedSnapshotManager snapshotManager;
  private int cursor;
  private int line;
  private int offset;

  public SimpleDocumentReader(String document) {
    this.document = document;
    snapshotManager =
        new ReferenceBasedSnapshotManager(
            this,
            snapshot -> {
              cursor = snapshot.cursor();
              line = snapshot.line();
              offset = snapshot.offset();
            });
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
    snapshotManager.reset();
  }

  @Override
  public char read() {
    expectMore(this);
    // Read one character and move the cursor.
    char character = charAt(cursor++);
    // Make sure we update the indices correctly.
    move(character);
    return character;
  }

  @Nullable
  @Override
  public String read(Pattern pattern, int group) {
    expectMore(this);
    checkNotNull(pattern, "pattern cannot be null");
    checkArgument(group >= 0, "group", "group must be non-negative.");
    Matcher matcher = pattern.matcher(this);
    if (!matcher.find(cursor) || matcher.start() != cursor) {
      return null;
    }
    // Move the cursor according to the entire substring that matched the pattern.
    String matched = matcher.group(0);
    cursor += matched.length();
    for (int i = 0; i < matched.length(); i++) {
      move(matched.charAt(i));
    }
    // Return only the part of the match the we are interested in.
    return matcher.group(group);
  }

  @Override
  public boolean has(Pattern pattern) {
    checkNotNull(pattern, "pattern cannot be null");
    return pattern.matcher(document).find(cursor());
  }

  @Override
  public DocumentReader rewind(int length) {
    expectDistance(this, length);
    // Scan the characters backward to make sure we are moving the line counter accordingly.
    for (int i = 0; i < length; i++) {
      cursor--;
      if (charAt(cursor) == '\n') {
        line--;
      }
    }
    // Seek from the current position to the beginning of the current line to determine the line
    // offset.
    int seek = cursor;
    offset = 1;
    while (seek > 0 && charAt(seek - 1) != '\n') {
      offset++;
      seek--;
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
}
