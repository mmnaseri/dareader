package com.mmnaseri.utils.dareader.exp;

import com.mmnaseri.utils.dareader.DocumentSnapshot;
import com.mmnaseri.utils.dareader.DocumentTokenizer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.mmnaseri.utils.dareader.exp.Expression.compose;
import static com.mmnaseri.utils.dareader.exp.Expression.empty;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/** Reads an entire expression from the input document. */
@FunctionalInterface
public interface ExpressionReader {

  /**
   * Reads the expression from the document or returns {@code null} if the tokenizer doesn't yield a
   * valid expression.
   */
  @Nullable
  Expression read(DocumentTokenizer tokenizer);

  /**
   * Returns a reader that first attempts to read the current definition. If not, it attempts to
   * read the other definition. If none matches, the document is reset.
   */
  default ExpressionReader or(ExpressionReader other) {
    return tokenizer -> {
      DocumentSnapshot snapshot = tokenizer.snapshot().create();
      Expression expression = read(tokenizer);
      if (expression != null) {
        return expression;
      }
      tokenizer.snapshot().restore(snapshot);
      expression = other.read(tokenizer);
      if (expression == null) {
        tokenizer.snapshot().restore(snapshot);
        return null;
      }
      return expression;
    };
  }

  /**
   * Reads both the current definition as well as the other definition sequentially. If both
   * matches, returns a composition. If either fails, resets the document and returns {@code null}.
   */
  default ExpressionReader and(ExpressionReader other) {
    return reader -> {
      DocumentSnapshot snapshot = reader.snapshot().create();
      Expression first = read(reader);
      if (first == null) {
        reader.snapshot().restore(snapshot);
        return null;
      }
      Expression second = other.read(reader);
      if (second == null) {
        reader.snapshot().restore(snapshot);
        return null;
      }
      List<Expression> left;
      List<Expression> right;
      if (ExpressionType.COMPOSITE.equals(first.type())) {
        left = first.children();
      } else {
        left = singletonList(first);
      }
      if (ExpressionType.COMPOSITE.equals(second.type())) {
        right = second.children();
      } else {
        right = singletonList(second);
      }
      return compose(Stream.concat(left.stream(), right.stream()).collect(toList()));
    };
  }

  /**
   * Returns a reader that succeeds if this reader fails. If that reader succeeds, an empty
   * expression with type {@link ExpressionType#EMPTY} will be created.
   *
   * @see #negate(ExpressionType)
   */
  default ExpressionReader negate() {
    return negate(ExpressionType.EMPTY);
  }

  /**
   * Returns a reader that succeeds if this reader fails. If that reader succeeds, an empty
   * expression with the indicated type will be created.
   */
  default ExpressionReader negate(ExpressionType type) {
    return tokenizer -> {
      DocumentSnapshot snapshot = tokenizer.snapshot().create();
      Expression expression = read(tokenizer);
      tokenizer.snapshot().restore(snapshot);
      if (expression == null) {
        return Expression.newBuilder(type).build();
      } else {
        return null;
      }
    };
  }

  /**
   * Returns a reader that returns an optional type expression. If the current reader succeeds, the
   * optional will have a single child that is the matching expression.
   */
  default ExpressionReader optional() {
    return tokenizer -> Expression.optional(read(tokenizer));
  }

  /**
   * Returns a reader that succeeds if the current reader can be used to glob the document exactly
   * the indicated number of times.
   */
  default ExpressionReader times(int exactly) {
    return tokenizer -> {
      DocumentSnapshot snapshot = tokenizer.snapshot().create();
      List<Expression> list = new ArrayList<>();
      for (int i = 0; i < exactly; i++) {
        Expression expression = read(tokenizer);
        if (expression == null) {
          tokenizer.snapshot().restore(snapshot);
          return null;
        }
        list.add(expression);
      }
      return compose(list);
    };
  }

  /**
   * Returns a reader that can be used to glob the document any number of times. If no matches are
   * found, returns an empty expression.
   */
  default ExpressionReader repeated() {
    return tokenizer -> {
      List<Expression> list = new ArrayList<>();
      while (true) {
        DocumentSnapshot snapshot = tokenizer.snapshot().create();
        Expression expression = read(tokenizer);
        if (expression == null) {
          tokenizer.snapshot().restore(snapshot);
          break;
        }
        list.add(expression);
      }
      if (list.isEmpty()) {
        return empty();
      }
      return compose(list);
    };
  }

  /**
   * Returns a reader that must glob the document at least once. The result will be a composite
   * expression which contains all iterations.
   */
  default ExpressionReader atLeastOnce() {
    return and(repeated())
        .then(
            expression -> {
              if (expression.children().size() != 2
                  && !expression.children(1).type().equals(ExpressionType.EMPTY)) {
                return expression;
              }
              return expression.toBuilder().removeChild(1).build();
            });
  }

  /**
   * Attempts to read the current expression definition and if it succeeds, calls the transformer
   * callback on the expression.
   */
  default ExpressionReader then(Function<Expression, Expression> transformer) {
    return tokenizer -> {
      DocumentSnapshot snapshot = tokenizer.snapshot().create();
      Expression expression = read(tokenizer);
      if (expression == null) {
        tokenizer.snapshot().restore(snapshot);
        return null;
      }
      return transformer.apply(expression);
    };
  }
}
