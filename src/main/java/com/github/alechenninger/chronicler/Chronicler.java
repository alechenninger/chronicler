package com.github.alechenninger.chronicler;

import com.github.alechenninger.chronicler.console.Exit;
import com.github.alechenninger.chronicler.console.Prompter;

import com.rallydev.rest.RallyRestApi;

import java.io.IOException;

public class Chronicler {

  public static void main(String[] args) throws Exception {
    ChroniclerOptions options = new ChroniclerOptions(args);

    if (options.helpRequested()) {
      ChroniclerOptions.printHelpMessage();
      return;
    }

    TimeSheetFactory sheetFactory = TimeSheetFactory.fromServiceLoaderInJar(options.sourcePlugin());
    TimeSheet timeSheet = sheetFactory.getTimeSheet(options.additionalArgs());

    // TODO: This can be cleaned up quite a bit
    RallyRestApi rally = new RallyRestApi(options.server(), options.apiKey());
    rally.setWsapiVersion("v2.0");

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        rally.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));

    TimeSheetUploader uploader = new RallyTimeSheetUploader(rally, options.user(),
        options.workspace(), Prompter.systemPrompt(), Exit.systemExit());

    uploader.uploadTimeSheet(timeSheet);

    System.out.println("The Chronicler has successfully recorded your time sheet.");
  }
}
