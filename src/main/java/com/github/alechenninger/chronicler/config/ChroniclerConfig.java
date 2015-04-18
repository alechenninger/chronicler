package com.github.alechenninger.chronicler.config;

import com.github.alechenninger.chronicler.ChroniclerException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public interface ChroniclerConfig {
  /**
   * @throws ChroniclerException If no api key is defined.
   */
  String apiKey();

  /**
   * @throws ChroniclerException If no server is defined.
   */
  URI server() throws URISyntaxException;

  /**
   * @throws ChroniclerException If no sourcePlugin is defined.
   */
  Path sourcePlugin();

  /**
   * @throws ChroniclerException If no user is defined.
   */
  String user();

  /**
   * @throws ChroniclerException If no workspace is defined.
   */
  String workspace();

  String[] pluginArgs();
}
