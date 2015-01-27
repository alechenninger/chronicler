package com.github.alechenninger.chronicler.console;

public interface Exit {
  /**
   * Exits the VM; never actually returns anything because the code to return something will never
   * execute. Only returns a value so you can satisfy dumb compilers.
   */
  <T> T exitWithStatus(int status);

  public static Exit systemExit() {
    return new Exit() {
      @Override
      public <T> T exitWithStatus(int status) {
        System.exit(status);
        return null;
      }
    };
  }
}
