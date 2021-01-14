package com.mmnaseri.utils.dareader.token;

/**
 * This interface represents a token type that can be projected back to its original form.
 * Technically, if {@code f(x)} is the parsing function, and {@code x} is the document, and all the
 * tokens coming off of the document are {@link ProjectableTokenType}s, if {@code g(y)} is the
 * concatenation function where {@code y} is a list of all tokens in the order in which they were
 * read, then {@code f(g(f(x))) = f(x)}.
 */
public interface ProjectableTokenType extends TokenType {

  /**
   * Projects the token that is given. The identity that {@code token.type() == this.type()} should
   * always hold when calling this method.
   */
  String project(Token token);
}
