package com.github.alechenninger.chronicler.console;

public class CaseSensitiveAnswer implements Answer {
  private final String answer;

  public CaseSensitiveAnswer(String answer) {
    this.answer = answer;
  }

  @Override
  public boolean isSatisfiedBy(String input) {
    return answer.equals(input);
  }

  @Override
  public String describe() {
    return answer;
  }
}
