package com.mmnaseri.utils.dareader.exp;

import com.mmnaseri.utils.dareader.token.Token;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.mmnaseri.utils.dareader.utils.Precondition.checkArgument;
import static com.mmnaseri.utils.dareader.utils.Precondition.checkNotNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * A simple class that is designed to determine a node in an expression tree formed when parsing the
 * abstract syntax tree of a given input document.
 */
public class Expression implements Cloneable {

  private ExpressionType type;
  private List<Expression> children;
  private List<Token> tokens;
  private Expression parent;

  private Expression() {}

  /** The type of expression in the tree that this instance represents. */
  @Nonnull
  public ExpressionType type() {
    return type;
  }

  /** Children of this expression. */
  @Nonnull
  public List<Expression> children() {
    return Collections.unmodifiableList(children);
  }

  /** Returns the child at the given index. */
  @Nonnull
  public Expression children(int index) {
    checkArgument(
        index >= 0 && index < children.size(),
        "index",
        "Index out of range: index: %d, size: %s",
        index,
        tokens.size());
    return children.get(index);
  }

  /** Direct tokens read to comprise this node. This does not include tokens from child nodes. */
  @Nonnull
  public List<Token> tokens() {
    return Collections.unmodifiableList(tokens);
  }

  @Nonnull
  public Token tokens(int index) {
    checkArgument(
        index >= 0 && index < tokens.size(),
        "index",
        "Index out of range: index: %d, size: %s",
        index,
        tokens.size());
    return tokens.get(index);
  }

  /** Parent node for the expression. Will be {@code null} for the root node. */
  @Nullable
  public Expression parent() {
    return parent;
  }

  /**
   * Returns the parent node. If no parent is present, this results in a {@link
   * NullPointerException}.
   */
  @Nonnull
  public Expression parentNonNull() {
    return checkNotNull(parent, "Expected parent to not be null");
  }

  /** Updates the parent of the same instance. */
  public Expression withParent(Expression parent) {
    return toBuilder().parent(parent).prepare();
  }

  /**
   * Updates the same instance by adding a child to it.
   *
   * <p>The distinction between this method and using {@code toBuilder().addChild().build()} is that
   * this method adds a child to the same node, whereas by calling {@link Builder#build()} you get a
   * new instance.
   */
  public Expression withChild(Expression child) {
    return toBuilder().addChild(checkNotNull(child, "child cannot be null")).prepare();
  }

  /** Indicates if this is a leaf node, i.e. has no children. */
  public boolean isLeaf() {
    return children().isEmpty();
  }

  /** Indicates if this is a root node, i.e. has no parent. */
  public boolean isRoot() {
    return parent() == null;
  }

  /** Returns a build which wraps a copy of this node. */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Expression)) {
      return false;
    }
    Expression that = (Expression) o;
    return type.equals(that.type) && children.equals(that.children) && tokens.equals(that.tokens);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, children, tokens, parent);
  }

  @Override
  public String toString() {
    return MessageFormat.format(
        "Expression'{'type={0},children={1},tokens={2}'}'", type, children, tokens);
  }

  /** Creates a new expression builder and sets the type. */
  public static Builder newBuilder(ExpressionType type) {
    return new Builder(new Expression()).type(type);
  }

  public static Expression compose(Iterable<Expression> expressions) {
    return Expression.newBuilder(ExpressionType.COMPOSITE)
        .addAllChildren(stream(expressions.spliterator(), /* parallel= */ false).collect(toList()))
        .build();
  }

  public static Expression compose(Expression... expressions) {
    return compose(Arrays.asList(expressions));
  }

  public static Expression optional() {
    return optional(null);
  }

  public static Expression optional(Expression other) {
    Builder builder = Expression.newBuilder(ExpressionType.OPTIONAL);
    if (other != null) {
      builder.addChild(other);
    }
    return builder.build();
  }

  public static Expression empty() {
    return Expression.newBuilder(ExpressionType.EMPTY).build();
  }

  /** Builder for {@link Expression}. */
  public static class Builder {

    private final Expression subject;

    public Builder(Expression subject) {
      this.subject = subject;
      this.subject.children = subject.children == null ? new ArrayList<>() : subject.children;
      this.subject.tokens = subject.tokens == null ? new ArrayList<>() : subject.tokens;
    }

    @Nonnull
    public ExpressionType type() {
      return subject.type();
    }

    /** Sets the type of the expression. */
    public Builder type(@Nonnull ExpressionType type) {
      checkNotNull(type, "type cannot be null");
      subject.type = type;
      return this;
    }

    @Nonnull
    public List<Expression> children() {
      return subject.children();
    }

    /** Overrides the children. */
    public Builder children(@Nonnull List<Expression> children) {
      subject.children.clear();
      return addAllChildren(children);
    }

    /** Adds the indicated children to this node. */
    public Builder addAllChildren(@Nonnull List<Expression> children) {
      checkNotNull(children, "children cannot be null");
      children.forEach(this::addChild);
      return this;
    }

    /** Adds a child to the node and updates the child to recognize this node as its parent. */
    public Builder addChild(@Nonnull Expression child) {
      checkNotNull(child, "child cannot be null");
      subject.children.add(child.withParent(subject));
      return this;
    }

    /** Removes the child at the indicated index. */
    public Builder removeChild(int index) {
      checkArgument(
          index >= 0 && index < subject.children.size(),
          "index",
          "Index was not between 0 and %d",
          subject.children.size());
      subject.children.remove(index);
      return this;
    }

    @Nonnull
    public List<Token> tokens() {
      return subject.tokens();
    }

    /** Overrides the existing tokens specified for this node. */
    public Builder tokens(@Nonnull List<Token> tokens) {
      subject.tokens.clear();
      return addAllTokens(tokens);
    }

    /** Appends the indicated tokens to the list of tokens for this node. */
    public Builder addAllTokens(@Nonnull List<Token> tokens) {
      checkNotNull(tokens, "tokens cannot be null");
      tokens.forEach(this::addToken);
      return this;
    }

    /** Adds a new token. */
    public Builder addToken(@Nonnull Token token) {
      checkNotNull(token, "token cannot be null");
      subject.tokens.add(token);
      return this;
    }

    /** Removes the token at the indicated index. */
    public Builder removeToken(int index) {
      checkArgument(
          index >= 0 && index < subject.tokens.size(),
          "index",
          "Index was not between 0 and %d",
          subject.tokens.size());
      subject.tokens.remove(index);
      return this;
    }

    @Nullable
    public Expression parent() {
      return subject.parent();
    }

    /** Updates the parent of this node. */
    public Builder parent(@Nullable Expression parent) {
      subject.parent = parent;
      return this;
    }

    /** Returns the malleable instance used by this builder. */
    private Expression prepare() {
      checkNotNull(subject.type, "Expression must have a type");
      return subject;
    }

    /** Builds a new instance of {@link Expression} with the properties set on this builder. */
    public Expression build() {
      Expression prepared = prepare();
      Expression expression = new Expression();
      expression.parent = prepared.parent();
      expression.type = prepared.type();
      expression.tokens = new ArrayList<>(prepared.tokens());
      expression.children = new ArrayList<>(prepared.children());
      return expression;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Builder)) {
        return false;
      }
      Builder builder = (Builder) o;
      return subject.equals(builder.subject);
    }

    @Override
    public int hashCode() {
      return Objects.hash(subject);
    }

    @Override
    public String toString() {
      return "Builder{" + subject + "}";
    }
  }
}
