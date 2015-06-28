package com.github.alechenninger.chronicler.config;

import com.github.alechenninger.chronicler.ChroniclerException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public interface ChroniclerConfig {
  /**
   * @throws java.util.NoSuchElementException If no api key is defined.
   */
  String apiKey();

  /**
   * @throws java.util.NoSuchElementException If no server is defined.
   */
  URI server() throws URISyntaxException;

  /**
   * @throws java.util.NoSuchElementException If no sourcePlugin is defined.
   */
  Path sourcePlugin();

  /**
   * @throws java.util.NoSuchElementException If no user is defined.
   */
  String user();

  /**
   * @throws java.util.NoSuchElementException If no workspace is defined.
   */
  String workspace();

  String[] pluginArgs();
}
