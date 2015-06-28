package com.github.alechenninger.chronicler;

import org.apache.commons.cli.Options;

public interface PluginInfo {
  String url();
  String name();
  Options cmdLineOptions();
  String version();
  String exampleUsage();
  String description();
}
