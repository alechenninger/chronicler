package com.github.alechenninger.chronicler;

import com.github.alechenninger.chronicler.config.ChroniclerConfig;
import com.github.alechenninger.chronicler.config.CmdLineChroniclerConfig;
import com.github.alechenninger.chronicler.config.ConfigFactory;
import com.github.alechenninger.chronicler.rally.RallyExternalTimeSheet;

import org.apache.commons.cli.ParseException;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class Main {
  private static final ConfigFactory configFactory = new ConfigFactory();
  private static final String VERSION = "3.1.2-SNAPSHOT";
  private static final Logger log = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) throws Exception {
    ChroniclerConfig config = configFactory.fromCommandLine(args);
    Optional<Plugin> maybePlugin = tryGetPlugin(config);

    Runnable app = getRunnable(args, config, maybePlugin);

    app.run();
  }

  private static Optional<Plugin> tryGetPlugin(ChroniclerConfig config) throws MalformedURLException {
    try {
      Path toSourcePlugin = config.sourcePlugin();
      ExternalServiceFactory extSvcFactory = new ServiceLoaderExternalServiceFactory(toSourcePlugin);
      return extSvcFactory.getService(Plugin.class);
    } catch (NoSuchElementException e) {
      return Optional.empty();
    } catch (FileNotFoundException e) {
      log.warning(e.toString());
      return Optional.empty();
    }
  }

  private static Runnable getRunnable(String[] args, ChroniclerConfig config,
      Optional<Plugin> maybePlugin) throws ParseException, URISyntaxException {
    if (CmdLineChroniclerConfig.helpRequested(args)) {
      return new HelpPrinter(maybePlugin);
    }

    if (CmdLineChroniclerConfig.versionRequested(args)) {
      return new VersionPrinter(VERSION, maybePlugin);
    }

    Plugin plugin = maybePlugin.orElseThrow(() -> new ChroniclerException("No Plugin.class " +
        "implementation found.\nMake sure source plugin has an implementation's FQN inside " +
        "META-INF/services/com.github.alechenninger.chronicler.Plugin"));

    TimeSheetFactory timeSheetFactory = plugin.timeSheetFactory();
    ExternalTimeSheet uploader = new RallyExternalTimeSheet(config.server(), config.apiKey(),
        config.user(), config.workspace());

    return new Chronicler(uploader, timeSheetFactory, config.pluginArgs());
  }
}
