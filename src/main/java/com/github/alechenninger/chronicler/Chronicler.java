package com.github.alechenninger.chronicler;

import com.github.alechenninger.chronicler.console.Exit;
import com.github.alechenninger.chronicler.console.Prompter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rallydev.rest.RallyRestApi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Chronicler {
  private static final ObjectMapper mapper = new ObjectMapper();

  private static final Path userHome = Paths.get(System.getProperty("user.home"));
  private static final Path defaultConfigPath = userHome.resolve("/.chronicler/config.json");

  public static void main(String[] args) throws Exception {
    CmdLineChroniclerOptions cmdLineOpts = new CmdLineChroniclerOptions(args);

    if (cmdLineOpts.helpRequested()) {
      CmdLineChroniclerOptions.printHelpMessage();
      return;
    }

    ChroniclerOptions options = getCombinedOptionsFromJsonConfigIfPresent(cmdLineOpts);
    TimeSheet timeSheet = getTimeSheet(options, cmdLineOpts.additionalArgs());
    uploadTimeSheet(timeSheet, options);

    System.out.println("The Chronicler has successfully recorded your time sheet.");
  }

  /** Prefers command line options if they are provided. */
  private static ChroniclerOptions getCombinedOptionsFromJsonConfigIfPresent(
      CmdLineChroniclerOptions cmdLineOptions) throws IOException {
    File configJson = cmdLineOptions.config().orElse(defaultConfigPath).toFile();

    if (configJson.exists()) {
      return mapper.readValue(configJson, SerializableChroniclerOptions.class)
          .overridedWith(cmdLineOptions);
    }

    return cmdLineOptions;
  }

  // TODO: Allow plugin args to be configured as well
  private static TimeSheet getTimeSheet(ChroniclerOptions options, String[] additionalArgs)
      throws MalformedURLException {
    TimeSheetFactory sheetFactory = TimeSheetFactory.fromServiceLoaderInJar(options.sourcePlugin());
    return sheetFactory.getTimeSheet(additionalArgs);
  }

  private static void uploadTimeSheet(TimeSheet timeSheet, ChroniclerOptions options) throws
      URISyntaxException {
    RallyRestApi rally = getRallyRestApi(options);
    TimeSheetUploader uploader = new RallyTimeSheetUploader(rally, options.user(),
        options.workspace(), Prompter.systemPrompt(), Exit.systemExit());
    uploader.uploadTimeSheet(timeSheet);
  }

  private static RallyRestApi getRallyRestApi(ChroniclerOptions options) throws URISyntaxException {
    RallyRestApi rally = new RallyRestApi(options.server(), options.apiKey());
    rally.setWsapiVersion("v2.0");

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        rally.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));

    return rally;
  }
}
