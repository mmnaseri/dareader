package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.dareader.DocumentSnapshotManager;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkArgument;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;

/** A simple immutable {@link DocumentSnapshot}. */
public class ImmutableSnapshot implements DocumentSnapshot {

  private final DocumentSnapshotManager manager;
  private final int line;
  private final int offset;
  private final int cursor;
  private final boolean hasNext;

  public ImmutableSnapshot(
      DocumentSnapshotManager manager, int line, int offset, int cursor, boolean hasNext) {
    checkArgument(line > 0, "line", "line has to be greater than 0");
    checkArgument(offset > 0, "offset", "offset has to be greater than 0");
    checkArgument(cursor >= 0, "curser", "cursor has to be greater than or equal to 0");
    this.manager = checkNotNull(manager, "manager cannot be null");
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

  @Override
  public boolean belongsTo(DocumentSnapshotManager manager) {
    return manager == this.manager;
  }

  @Override
  public void apply() {
    manager.restore(this);
  }
}
