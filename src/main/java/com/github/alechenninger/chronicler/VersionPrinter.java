package com.github.alechenninger.chronicler;

import java.util.Optional;

public class VersionPrinter implements Runnable {
  private final Optional<Plugin> maybePlugin;
  private final String chroniclerVersion;

  public VersionPrinter(String chroniclerVersion, Optional<Plugin> maybePlugin) {
    this.maybePlugin = maybePlugin;
    this.chroniclerVersion = chroniclerVersion;
  }

  @Override
  public void run() {
    System.out.println("chronicler version: " + chroniclerVersion);

    if (maybePlugin.isPresent()) {
      PluginInfo pluginInfo = maybePlugin.get().info();

      System.out.println(pluginInfo.name() + " version: " + pluginInfo.version());
    }
  }
}
