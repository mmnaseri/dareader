package com.mmnaseri.utils.dareader.utils;

/** Simple class that can be used to check preconditions. */
public final class Precondition {

  private Precondition() throws IllegalAccessException {
    throw new IllegalAccessException("This type should not be instantiated.");
  }

  /**
   * Checks the state of the given parameter. If the input is {@code false}, it will throw an {@link
   * IllegalStateException} with no message.
   */
  public static void checkState(boolean state) {
    checkState(state, null);
  }

  /**
   * Checks the state of the given parameter. If the input is {@code false}, it will throw an {@link
   * IllegalStateException} with the provided message and arguments. If the message is {@code null},
   * the exception will not have a message.
   */
  public static void checkState(boolean state, String message, Object... arguments) {
    if (!state) {
      if (message != null) {
        throw new IllegalStateException(String.format(message, arguments));
      } else {
        throw new IllegalStateException();
      }
    }
  }

  /**
   * Checks the given condition and if it doesn't match, throws an {@link IllegalArgumentException}.
   */
  public static void checkArgument(boolean condition, String name) {
    checkArgument(condition, name, null);
  }

  /**
   * Checks the given condition and if it doesn't match, throws an {@link IllegalArgumentException}.
   */
  public static void checkArgument(
      boolean condition, String name, String message, Object... arguments) {
    if (!condition) {
      if (message != null) {
        throw new IllegalArgumentException(
            String.format("Error while checking %s: %s", name, String.format(message, arguments)));
      } else {
        throw new IllegalArgumentException(String.format("Error while checking %s", name));
      }
    }
  }

  /**
   * Checks the given value. If it is {@code null}, throws a {@link NullPointerException}. If the
   * object is not {@link null}, returns it intact.
   */
  public static <E> E checkNotNull(E value) {
    return checkNotNull(value, null);
  }

  /**
   * Checks the given value. If it is {@code null}, throws a {@link NullPointerException}. If the
   * object is not {@link null}, returns it intact.
   */
  public static <E> E checkNotNull(E value, String message, Object... arguments) {
    if (value != null) {
      return value;
    }
    if (message == null) {
      throw new NullPointerException();
    } else {
      throw new NullPointerException(String.format(message, arguments));
    }
  }

}
