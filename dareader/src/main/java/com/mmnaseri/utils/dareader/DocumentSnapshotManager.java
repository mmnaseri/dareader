package com.mmnaseri.utils.dareader;

/** A snapshot manager that can be used to control and check the snapshots for a given document. */
public interface DocumentSnapshotManager {

  /** Creates and returns a snapshot of the current reader. */
  DocumentSnapshot create();

  /** Restores this document reader to the state represented by the snapshot. */
  DocumentReader restore(DocumentSnapshot snapshot);

  /**
   * Indicates whether or not a given snapshot belongs to this manager.
   *
   * <p>Essentially {@code knows(create()) == true} must hold all the time. Moreover, if this method
   * returns {@code true}, it should be enough of an indication that the snapshot was created by
   * calling {@link #create()} on the same instance.
   */
  boolean knows(DocumentSnapshot snapshot);
}
