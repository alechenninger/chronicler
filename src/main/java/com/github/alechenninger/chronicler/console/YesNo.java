package com.github.alechenninger.chronicler.console;

public enum YesNo implements Answer {
  YES(new CaseSensitiveAnswer("Y")), NO(new CaseSensitiveAnswer("n"));

  private final Answer answer;

  YesNo(Answer answer) {
    this.answer = answer;
  }

  @Override
  public boolean isSatisfiedBy(String input) {
    return answer.isSatisfiedBy(input);
  }

  @Override
  public String describe() {
    return answer.describe();
  }

  public static Choice<Boolean> asChoice() {
    return new BooleanChoice(YES, NO);
  }
}
