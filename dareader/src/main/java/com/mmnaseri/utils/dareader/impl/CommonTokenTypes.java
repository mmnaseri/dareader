package com.mmnaseri.utils.dareader.impl;

import com.mmnaseri.utils.dareader.ProjectableTokenType;
import com.mmnaseri.utils.dareader.Token;
import com.mmnaseri.utils.dareader.TokenTypeRegistry;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkState;
import static java.util.Arrays.stream;

/**
 * A list of the most common token types which are usually encountered in a given parsing
 * application. These tokens are usually projected back verbatim, so, that is the basic assumption
 * in this implementation.
 */
public enum CommonTokenTypes implements ProjectableTokenType {
  /** Signifies a continuous whitespace region read from the input. */
  WHITESPACE(10_000_001),
  /** Signifies a single set of digits with no signage. Example: {@code 123}. */
  UNSIGNED_INT(10_000_002),
  /**
   * Signifies a single set of digits with a leading signage. Example: {@code -123}, {@code +123}.
   */
  SIGNED_INT(10_000_003),
  /** Signifies two sets of digits separated by a dot with no signage. Example: {@code 123.456}. */
  UNSIGNED_FLOAT(10_000_004),
  /**
   * Signifies two sets of digits separated by a dot with a leading signage. Example: {@code
   * -123.456}, {@code +123.456}.
   */
  SIGNED_FLOAT(10_000_005),
  /** Signifies a constant value meaningful to the application. Example: {@code true}. */
  CONSTANT(10_000_006),
  /** Signifies an operator read from the input. Example: {@code +}. */
  OPERATOR(10_000_007);

  private final int tag;

  CommonTokenTypes(final int tag) {
    this.tag = tag;
  }

  @Override
  public int tag() {
    return tag;
  }

  @Override
  public String project(final Token token) {
    checkNotNull(token, "Token cannot be null");
    checkState(
        equals(token.type()),
        "Cannot project a token of type %s with token type %s",
        token.type(),
        this);
    return token.value();
  }

  /** Returns a {@link TokenTypeRegistry} that contains all the tags specified here. */
  public static TokenTypeRegistry registry() {
    return REGISTRY;
  }

  private static final TokenTypeRegistry REGISTRY = new SimpleTokenTypeRegistry();

  static {
    stream(values()).forEach(REGISTRY::add);
  }
}
