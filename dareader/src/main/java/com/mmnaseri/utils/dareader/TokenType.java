package com.mmnaseri.utils.dareader;

/**
 * A token type which can be used to identify different tokens appearing in a string document.
 *
 * <p>A token type will act as a fly-wheel, therefore, each token type instance is a singleton that
 * will be accessed from the {@link TokenTypeRegistry}. Therefore, it is important that token types
 * have a meaningful {@link #hashCode()} and {@link #equals(Object)} implementation that can
 * distinguish them on a per-instance basis.
 *
 * <p>It would also be a good idea for instances of this class to have a decent {@link #toString()}
 * implementation, since they will be used in error reporting.
 */
public interface TokenType {

  /** Returns a number that can be used to address the token type. */
  int tag();
}
