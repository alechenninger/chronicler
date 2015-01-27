package com.github.alechenninger.chronicler.console;

import java.util.Optional;

public class BooleanChoice implements Choice<Boolean> {
  private final Answer trueAnswer;
  private final Answer falseAnswer;

  public BooleanChoice(Answer trueAnswer, Answer falseAnswer) {
    this.trueAnswer = trueAnswer;
    this.falseAnswer = falseAnswer;
  }

  @Override
  public Optional<Boolean> parseAnswer(String input) {
    if (trueAnswer.isSatisfiedBy(input)) {
      return Optional.of(true);
    }

    if (falseAnswer.isSatisfiedBy(input)) {
      return Optional.of(false);
    }

    return Optional.empty();
  }

  @Override
  public String describe() {
    return trueAnswer.describe() + "/" + falseAnswer.describe();
  }
}
