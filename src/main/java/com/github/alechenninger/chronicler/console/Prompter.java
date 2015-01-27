package com.github.alechenninger.chronicler.console;

import java.util.Optional;

public interface Prompter {
  public static Prompter systemPrompt() {
    return new BasicPrompter(System.in, System.out);
  }

  String promptForInput(String prompt);

  default <T extends Choice<A>, A> A promptForInput(String prompt, T choice) {
    Optional<A> answer = choice.parseAnswer(promptForInput(prompt));

    if (!answer.isPresent()) {
      return promptForInput("Invalid response.\n" + prompt, choice);
    }

    return answer.get();
  }

}
