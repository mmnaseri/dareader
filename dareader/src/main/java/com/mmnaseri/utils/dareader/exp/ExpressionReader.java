package com.mmnaseri.utils.dareader.exp;

import com.mmnaseri.utils.dareader.DocumentReader;
import com.mmnaseri.utils.dareader.DocumentSnapshot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.mmnaseri.utils.dareader.exp.Expression.compose;
import static com.mmnaseri.utils.dareader.exp.Expression.empty;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/** Reads an entire expression from the input document. */
@FunctionalInterface
public interface ExpressionReader {

  /**
   * Reads the expression from the document or returns {@code null} if the reader doesn't yield a
   * valid expression.
   */
  @Nullable
  Expression read(DocumentReader reader);

  default ExpressionReader or(ExpressionReader other) {
    return reader -> {
      DocumentSnapshot snapshot = reader.snapshot().create();
      Expression expression = read(reader);
      if (expression != null) {
        return expression;
      }
      reader.snapshot().restore(snapshot);
      expression = other.read(reader);
      if (expression == null) {
        reader.snapshot().restore(snapshot);
        return null;
      }
      return expression;
    };
  }

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

  default ExpressionReader negate(ExpressionType type) {
    return reader -> {
      DocumentSnapshot snapshot = reader.snapshot().create();
      Expression expression = read(reader);
      reader.snapshot().restore(snapshot);
      if (expression == null) {
        return Expression.newBuilder().type(type).build();
      } else {
        return null;
      }
    };
  }

  default ExpressionReader optional() {
    return reader -> Expression.optional(read(reader));
  }

  default ExpressionReader times(int exactly) {
    return reader -> {
      DocumentSnapshot snapshot = reader.snapshot().create();
      List<Expression> list = new ArrayList<>();
      for (int i = 0; i < exactly; i++) {
        Expression expression = read(reader);
        if (expression == null) {
          reader.snapshot().restore(snapshot);
          return null;
        }
        list.add(expression);
      }
      return compose(list);
    };
  }

  default ExpressionReader repeated() {
    return reader -> {
      List<Expression> list = new ArrayList<>();
      while (true) {
        DocumentSnapshot snapshot = reader.snapshot().create();
        Expression expression = read(reader);
        if (expression == null) {
          reader.snapshot().restore(snapshot);
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

  default ExpressionReader atLeastOnce() {
    return and(repeated());
  }
}
