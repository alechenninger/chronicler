package com.github.alechenninger.chronicler;

import com.github.alechenninger.chronicler.config.ChroniclerConfig;
import com.github.alechenninger.chronicler.config.CmdLineChroniclerConfig;
import com.github.alechenninger.chronicler.config.ConfigFactory;
import com.github.alechenninger.chronicler.console.Exit;
import com.github.alechenninger.chronicler.console.Prompter;
import com.github.alechenninger.chronicler.rally.RallyTimeSheetUploader;

public class Chronicler {
  private final TimeSheetUploader uploader;
  private final TimeSheetFactory timeSheetFactory;
  private final String[] factoryArgs;

  public Chronicler(TimeSheetUploader uploader, TimeSheetFactory timeSheetFactory,
      String[] factoryArgs) {
    this.uploader = uploader;
    this.timeSheetFactory = timeSheetFactory;
    this.factoryArgs = factoryArgs;
  }

  public void chronicle() {
    TimeSheet timeSheet = timeSheetFactory.getTimeSheet(factoryArgs);
    uploader.uploadTimeSheet(timeSheet);
    System.out.println("The Chronicler has successfully recorded your time sheet.");
  }

  public static void main(String[] args) throws Exception {
    if (CmdLineChroniclerConfig.helpRequested(args)) {
      CmdLineChroniclerConfig.printHelpMessage();
      return;
    }

    ConfigFactory configFactory = new ConfigFactory();
    ChroniclerConfig config = configFactory.fromCommandLine(args);

    TimeSheetFactory timeSheetFactory = new ServiceLoaderTimeSheetFactory(
        config.sourcePlugin().toUri().toURL());
    TimeSheetUploader uploader = new RallyTimeSheetUploader(config.server(), config.apiKey(),
        config.user(), config.workspace(), Prompter.systemPrompt(), Exit.systemExit());

    Chronicler chronicler = new Chronicler(uploader, timeSheetFactory, config.pluginArgs());

    chronicler.chronicle();
  }
}
