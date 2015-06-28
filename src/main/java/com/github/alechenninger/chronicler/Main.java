package com.github.alechenninger.chronicler;

import com.github.alechenninger.chronicler.config.ChroniclerConfig;
import com.github.alechenninger.chronicler.config.CmdLineChroniclerConfig;
import com.github.alechenninger.chronicler.config.ConfigFactory;
import com.github.alechenninger.chronicler.console.Exit;
import com.github.alechenninger.chronicler.console.Prompter;
import com.github.alechenninger.chronicler.rally.RallyTimeSheetUploader;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;

public abstract class Main {
  private static final ConfigFactory configFactory = new ConfigFactory();
  private static final String VERSION = "3.0.0";

  public static void main(String[] args) throws Exception {
    ChroniclerConfig config = configFactory.fromCommandLine(args);
    Optional<Plugin> maybePlugin = tryGetPlugin(config);

    Runnable app;

    if (CmdLineChroniclerConfig.helpRequested(args)) {
      app = new HelpPrinter(maybePlugin);
    } else if (CmdLineChroniclerConfig.versionRequested(args)) {
      app = new VersionPrinter(VERSION, maybePlugin);
    } else {
      Plugin plugin = maybePlugin.orElseThrow(() -> new ChroniclerException(
          "No Plugin.class implementation found.\n"
              + "Make sure source plugin has an implementation's FQN inside "
              + "META-INF/services/com.github.alechenninger.chronicler.Plugin"));

      TimeSheetFactory timeSheetFactory = plugin.timeSheetFactory();
      TimeSheetUploader uploader = new RallyTimeSheetUploader(config.server(), config.apiKey(),
          config.user(), config.workspace(), Prompter.systemPrompt(), Exit.systemExit());

      app = new Chronicler(uploader, timeSheetFactory, config.pluginArgs());
    }

    app.run();
  }

  private static Optional<Plugin> tryGetPlugin(ChroniclerConfig config) throws MalformedURLException {
    try {
      Path toSourcePlugin = config.sourcePlugin();
      ExternalServiceFactory extSvcFactory = new ServiceLoaderExternalServiceFactory(toSourcePlugin);
      return extSvcFactory.getService(Plugin.class);
    } catch (NoSuchElementException e) {
      return Optional.empty();
    }
  }
}
