package com.github.alechenninger;

public class HamsterRally {

  public static void main(String[] args) throws Exception {
    HamsterRallyOptions options = new HamsterRallyOptions(args);

    if (options.helpRequested()) {
      HamsterRallyOptions.printHelpMessage();
      return;
    }

    TimeSheetFactory sheetFactory = TimeSheetFactory.byType(options.timeSheetType());
    TimeSheet timeSheet = sheetFactory.parseTimeSheet(options.additionalArgs());

    System.out.println(timeSheet);


  }
}
