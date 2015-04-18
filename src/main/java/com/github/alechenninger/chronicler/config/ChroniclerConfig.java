package com.github.alechenninger.chronicler.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public interface ChroniclerConfig {
  String apiKey();

  URI server() throws URISyntaxException;

  Path sourcePlugin();

  String user();

  String workspace();

  String[] pluginArgs();

  /**
   * @return A new {@link ChroniclerConfig} instance with non-{@code null} values of the provided
   * options being used preferentially.
   */
  default ChroniclerConfig overridedWith(ChroniclerConfig override) {
    return new ChroniclerConfig() {
      private final ChroniclerConfig original = ChroniclerConfig.this;

      @Override
      public String apiKey() {
        return override.apiKey() != null ? override.apiKey() : original.apiKey();
      }

      @Override
      public URI server() throws URISyntaxException {
        return override.server() != null ? override.server() : original.server();
      }

      @Override
      public Path sourcePlugin() {
        return override.sourcePlugin() != null ? override.sourcePlugin() : original.sourcePlugin();
      }

      @Override
      public String user() {
        return override.user() != null ? override.user() : original.user();
      }

      @Override
      public String workspace() {
        return override.workspace() != null ? override.workspace() : original.workspace();
      }

      @Override
      public String[] pluginArgs() {
        return override.pluginArgs() != null ? override.pluginArgs() : original.pluginArgs();
      }
    };
  }
}
