package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.DocumentReader;
import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.tuples.impl.ThreeTuple;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

/** Tests for {@link ReferenceBasedSnapshotManager}. */
public class ReferenceBasedSnapshotManagerTest {

  private DocumentReader reader;
  private List<ThreeTuple<Integer, Integer, Integer, Integer>> callbackCalls;
  private ReferenceBasedSnapshotManager manager;

  @BeforeMethod
  public void setUp() {
    reader = DocumentReader.of("abc\ndef\n123456");
    // Read the first line.
    reader.read("\\S+");
    reader.read();

    // Read the second line.
    reader.read("\\S+");
    reader.read();

    // Read three character.
    reader.read(".{3}");

    assertThat(reader.line(), is(3));
    assertThat(reader.offset(), is(4));

    callbackCalls = new ArrayList<>();
    manager =
        new ReferenceBasedSnapshotManager(
            reader,
            snapshot ->
                callbackCalls.add(
                    ThreeTuple.of(snapshot.cursor(), snapshot.line(), snapshot.offset())));
  }

  @Test
  public void createSnapshot() {
    DocumentSnapshot snapshot = manager.create();
    assertThat(snapshot, is(notNullValue()));
    assertThat(snapshot.cursor(), is(reader.cursor()));
    assertThat(snapshot.line(), is(reader.line()));
    assertThat(snapshot.offset(), is(reader.offset()));
    assertThat(snapshot.hasNext(), is(reader.hasNext()));
  }

  @Test
  public void createDuplicateSnapshot() {
    DocumentSnapshot snapshot = manager.create();
    assertThat(manager.create(), is(sameInstance(snapshot)));
  }

  @Test
  public void resetSnapshot() {
    DocumentSnapshot snapshot = manager.create();

    manager.reset();

    assertThat(manager.create(), is(not(sameInstance(snapshot))));
  }

  @Test
  public void ownedSnapshot() {
    assertThat(manager.knows(manager.create()), is(true));
  }

  @Test
  public void nonOwnedSnapshot() {
    assertThat(
        manager.knows(
            new ImmutableSnapshot(
                new ReferenceBasedSnapshotManager(reader, snapshot -> {}), 1, 1, 1, true)),
        is(false));
  }

  @Test
  public void restoring() {
    DocumentSnapshot snapshot = manager.create();

    while (reader.hasNext()) {
      reader.read();
    }
    assertThat(reader.hasNext(), is(false));

    snapshot.apply();

    assertThat(callbackCalls, hasSize(1));

    ThreeTuple<Integer, Integer, Integer, Integer> tuple = callbackCalls.get(0);
    assertThat(tuple.first(), is(snapshot.cursor()));
    assertThat(tuple.second(), is(snapshot.line()));
    assertThat(tuple.third(), is(snapshot.offset()));
  }
}
