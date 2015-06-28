package com.github.alechenninger.chronicler;

import com.github.alechenninger.chronicler.config.ChroniclerConfig;
import com.github.alechenninger.chronicler.config.CmdLineChroniclerConfig;
import com.github.alechenninger.chronicler.config.ConfigFactory;
import com.github.alechenninger.chronicler.console.Exit;
import com.github.alechenninger.chronicler.console.Prompter;
import com.github.alechenninger.chronicler.rally.RallyTimeSheetUploader;

public class Chronicler implements Runnable {
  private final TimeSheetUploader uploader;
  private final TimeSheetFactory timeSheetFactory;
  private final String[] factoryArgs;

  public Chronicler(TimeSheetUploader uploader, TimeSheetFactory timeSheetFactory,
      String[] factoryArgs) {
    this.uploader = uploader;
    this.timeSheetFactory = timeSheetFactory;
    this.factoryArgs = factoryArgs;
  }

  public void run() {
    TimeSheet timeSheet = timeSheetFactory.getTimeSheet(factoryArgs);
    uploader.uploadTimeSheet(timeSheet);
    System.out.println("The Chronicler has successfully recorded your time sheet.");
  }
}
