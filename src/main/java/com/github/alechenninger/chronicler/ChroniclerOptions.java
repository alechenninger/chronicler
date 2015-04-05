package com.github.alechenninger.chronicler;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public interface ChroniclerOptions {
  String apiKey();

  URI server() throws URISyntaxException;

  Path sourcePlugin();

  String user();

  String workspace();

  /**
   * @return A new {@link ChroniclerOptions} instance with non-{@code null} values of the provided
   * options being used preferentially.
   */
  default ChroniclerOptions overridedWith(ChroniclerOptions override) {
    return new ChroniclerOptions() {
      private final ChroniclerOptions original = ChroniclerOptions.this;

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
    };
  }
}
