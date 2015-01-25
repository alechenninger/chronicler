package com.github.alechenninger;

public class Chronicler {

  public static void main(String[] args) throws Exception {
    ChroniclerOptions options = new ChroniclerOptions(args);

    if (options.helpRequested()) {
      ChroniclerOptions.printHelpMessage();
      return;
    }

    TimeSheetFactory sheetFactory = TimeSheetFactory.byType(options.timeSheetType());
    TimeSheet timeSheet = sheetFactory.parseTimeSheet(options.additionalArgs());

    System.out.println(timeSheet);


  }
}
