package com.github.alechenninger.chronicler.console;

public interface Answer {
  boolean isSatisfiedBy(String input);
  String describe();
}
