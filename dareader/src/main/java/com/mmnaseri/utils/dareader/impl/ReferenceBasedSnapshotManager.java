package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.DocumentReader;
import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.dareader.DocumentSnapshotManager;

import java.util.function.Consumer;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkState;

/** A snapshot manager that uses the object reference to verify ownership. */
public class ReferenceBasedSnapshotManager implements DocumentSnapshotManager {

  private final DocumentReader reader;
  private final Consumer<DocumentSnapshot> restoreCallback;
  private DocumentSnapshot snapshot;

  public ReferenceBasedSnapshotManager(
      DocumentReader reader, Consumer<DocumentSnapshot> restoreCallback) {
    this.reader = reader;
    this.restoreCallback = restoreCallback;
  }

  @Override
  public DocumentSnapshot create() {
    if (snapshot == null) {
      snapshot =
          new ImmutableSnapshot(
              this, reader.line(), reader.offset(), reader.cursor(), reader.hasNext());
    }
    return snapshot;
  }

  @Override
  public DocumentReader restore(DocumentSnapshot snapshot) {
    checkNotNull(snapshot, "snapshot cannot be null");
    checkState(knows(snapshot), "The provided snapshot does not belong to this document.");
    restoreCallback.accept(snapshot);
    return reader;
  }

  @Override
  public boolean knows(DocumentSnapshot snapshot) {
    return snapshot.belongsTo(this);
  }

  /** Resets the cached snapshot to {@code null} so that a new one can be created if needed. */
  public void reset() {
    snapshot = null;
  }
}
