package com.github.alechenninger.chronicler.console;

import com.github.alechenninger.chronicler.ChroniclerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class BasicPrompter implements Prompter {
  private final BufferedReader in;
  private final PrintStream out;

  public BasicPrompter(InputStream in, PrintStream out) {
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = out;
  }

  @Override
  public String promptForInput(String prompt) {
    out.print(prompt + " ");

    try {
      return in.readLine();
    } catch (IOException e) {
      throw new ChroniclerException("Failed to read input.", e);
    }
  }
}
