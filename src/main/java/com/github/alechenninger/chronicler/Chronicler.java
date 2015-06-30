package com.github.alechenninger.chronicler;

public class Chronicler implements Runnable {
  private final ExternalTimeSheet uploader;
  private final TimeSheetFactory timeSheetFactory;
  private final String[] factoryArgs;

  public Chronicler(ExternalTimeSheet uploader, TimeSheetFactory timeSheetFactory,
      String[] factoryArgs) {
    this.uploader = uploader;
    this.timeSheetFactory = timeSheetFactory;
    this.factoryArgs = factoryArgs;
  }

  public void run() {
    TimeSheet timeSheet = uploader.getLastRecordedEntryTime()
        .map(e -> timeSheetFactory.getTimeSheet(factoryArgs, e))
        .orElseGet(() -> timeSheetFactory.getTimeSheet(factoryArgs));

    uploader.uploadTimeSheet(timeSheet);

    System.out.println("The Chronicler has successfully recorded your time sheet.");
  }
}
