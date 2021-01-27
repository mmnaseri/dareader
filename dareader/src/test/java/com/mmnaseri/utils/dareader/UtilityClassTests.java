package com.mmnaseri.utils.dareader;

import com.mmnaseri.utils.dareader.error.DocumentReaderExceptions;
import com.mmnaseri.utils.dareader.utils.Precondition;
import com.mmnaseri.utils.dareader.utils.TokenReaders;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.expectThrows;

public class UtilityClassTests {

  private static final List<Class<?>> UTILITY_CLASSES =
      Arrays.asList(DocumentReaderExceptions.class, TokenReaders.class, Precondition.class);

  @Test
  public void testConstruction() throws Exception {
    for (Class<?> utilityClass : UTILITY_CLASSES) {
      Constructor<?> constructor = utilityClass.getDeclaredConstructor();
      constructor.setAccessible(true);

      InvocationTargetException exception =
          expectThrows(InvocationTargetException.class, constructor::newInstance);

      assertThat(exception, is(notNullValue()));
      assertThat(exception.getCause(), is(instanceOf(IllegalAccessException.class)));
    }
  }
}
