package com.github.alechenninger.chronicler.console;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MultipleChoice<T extends Answer> implements Choice<T> {
  private final T[] answers;

  @SafeVarargs
  public MultipleChoice(T... answers) {
    this.answers = Objects.requireNonNull(answers, "answers");
  }

  @Override
  public Optional<T> parseAnswer(String input) {
    for (T answer : answers) {
      if (answer.isSatisfiedBy(input)) {
        return Optional.of(answer);
      }
    }

    return Optional.empty();
  }

  @Override
  public String describe() {
    return Arrays.stream(answers)
        .map(Answer::describe)
        .collect(Collectors.joining("/"));
  }
}
