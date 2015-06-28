package com.github.alechenninger.chronicler;

import com.github.alechenninger.chronicler.config.CmdLineChroniclerConfig;

import org.apache.commons.cli.HelpFormatter;

import java.util.Optional;

public class HelpPrinter implements Runnable {
  private final Optional<Plugin> maybePlugin;
  private final HelpFormatter helpFormatter = new HelpFormatter();

  public HelpPrinter(Optional<Plugin> maybePlugin) {
    this.maybePlugin = maybePlugin;
  }

  @Override
  public void run() {
    System.out.println("Chronicler:");
    CmdLineChroniclerConfig.printHelpMessage();

    if (maybePlugin.isPresent()) {
      System.out.println("");
      System.out.println("Source plugin:");
      PluginInfo pluginInfo = maybePlugin.get().info();
      helpFormatter.printHelp(pluginInfo.exampleUsage(),
          pluginInfo.description(),
          pluginInfo.cmdLineOptions(),
          pluginInfo.url(),
          true);
    }
  }
}
