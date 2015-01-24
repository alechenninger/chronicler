package com.github.alechenninger;

public class HamsterRallyException extends RuntimeException {
  public HamsterRallyException(String message) {
    super(message);
  }

  public HamsterRallyException(String message, Throwable cause) {
    super(message, cause);
  }

  public HamsterRallyException(Throwable cause) {
    super(cause);
  }
}
