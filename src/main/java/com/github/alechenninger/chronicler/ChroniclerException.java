package com.github.alechenninger.chronicler;

public class ChroniclerException extends RuntimeException {
  public ChroniclerException(String message) {
    super(message);
  }

  public ChroniclerException(String formatString, Object... args) {
    super(String.format(formatString, args));
  }

  public ChroniclerException(String message, Throwable cause) {
    super(message, cause);
  }

  public ChroniclerException(Throwable cause) {
    super(cause);
  }
}
