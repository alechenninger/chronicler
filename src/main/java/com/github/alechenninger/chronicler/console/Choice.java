package com.github.alechenninger.chronicler.console;

import java.util.Optional;

public interface Choice<T> {
  Optional<T> parseAnswer(String input);
  String describe();
}
