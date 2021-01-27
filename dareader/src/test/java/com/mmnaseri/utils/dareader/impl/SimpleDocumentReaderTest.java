package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.DocumentReader;
import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.dareader.error.DocumentReaderException;
import org.testng.annotations.Test;
import org.w3c.dom.ranges.DocumentRange;

import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.expectThrows;

/** Tests for {@link SimpleDocumentReader}. */
public class SimpleDocumentReaderTest {

  @Test
  public void readASingleCharacter() {
    DocumentReader reader = DocumentReader.of("ab\ncd");

    assertThat(reader.cursor(), is(0));
    assertThat(reader.line(), is(1));
    assertThat(reader.offset(), is(1));

    char read = reader.read();

    assertThat(read, is('a'));

    assertThat(reader.cursor(), is(1));
    assertThat(reader.line(), is(1));
    assertThat(reader.offset(), is(2));
  }

  @Test
  public void hasNext() {
    assertThat(DocumentReader.of("abc").hasNext(), is(true));
    assertThat(DocumentReader.of("").hasNext(), is(false));
  }

  @Test
  public void readAPattern() {
    DocumentReader reader = DocumentReader.of("a1\na2");

    String read = reader.read(Pattern.compile("[a-z]([0-9]+)\\s"));

    assertThat(read, is(notNullValue()));
    assertThat(read, is("a1\n"));

    assertThat(reader.cursor(), is(3));
    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(1));

    assertThat(reader.read("[a-z]([0-9]+)\\s"), is(nullValue()));

    assertThat(reader.cursor(), is(3));
    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(1));
  }

  @Test
  public void expectAPattern() {
    DocumentReader reader = DocumentReader.of("a1\na2");

    String read = reader.expect(Pattern.compile("[a-z]([0-9]+)\\s"));

    assertThat(read, is(notNullValue()));
    assertThat(read, is("a1\n"));

    assertThat(reader.cursor(), is(3));
    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(1));

    DocumentReaderException exception =
        expectThrows(DocumentReaderException.class, () -> reader.expect("[a-z]([0-9]+)\\s"));

    assertThat(exception.line(), is(2));
    assertThat(exception.offset(), is(1));

    assertThat(reader.cursor(), is(3));
    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(1));
  }

  @Test
  public void hasAPattern() {
    DocumentReader reader = DocumentReader.of("a1b2");

    assertThat(reader.has("a1b"), is(true));
    assertThat(reader.has("a1c"), is(false));
  }

  @Test
  public void toStringValue() {
    DocumentReader reader = DocumentReader.of("abc");

    assertThat(reader.toString(), is("^abc"));

    reader.read();
    assertThat(reader.toString(), is("a^bc"));

    reader.read();
    assertThat(reader.toString(), is("ab^c"));

    reader.read();
    assertThat(reader.toString(), is("abc^"));
  }

  @Test
  public void rewinding() {
    DocumentReader reader = DocumentReader.of("abc\ndef\n123\nhello");

    assertThat(reader.read("(.{3})\n", 1), is("abc"));
    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(1));

    assertThat(reader.read("(.{3})\n", 1), is("def"));
    assertThat(reader.line(), is(3));
    assertThat(reader.offset(), is(1));

    // Retract the '\n'.
    reader.rewind(1);

    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(4));

    // Retract the 'ef'.
    reader.rewind(2);

    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(2));

    // Retract the '\nd'.
    reader.rewind(2);

    assertThat(reader.line(), is(1));
    assertThat(reader.offset(), is(4));
  }

  @Test
  public void restoringSnapshot() {
    DocumentReader reader = DocumentReader.of("abc\ndef");
    reader.read(".{4}");
    DocumentSnapshot snapshot = reader.snapshot().create();

    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(1));
    assertThat(reader.hasNext(), is(true));

    assertThat(reader.read(".+"), is("def"));

    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(4));
    assertThat(reader.hasNext(), is(false));

    snapshot.apply();

    assertThat(reader.line(), is(2));
    assertThat(reader.offset(), is(1));
    assertThat(reader.hasNext(), is(true));

    assertThat(reader.read(".+"), is("def"));
    assertThat(reader.hasNext(), is(false));
  }
}
