package com.github.alechenninger.chronicler.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultChroniclerConfig implements ChroniclerConfig {
  private final String apiKey;
  private final URI server;
  private final String user;
  private final String workspace;
  private final Path sourcePlugin;
  private final String[] pluginArgs;

  @JsonCreator
  public DefaultChroniclerConfig(@JsonProperty("apiKey") String apiKey,
      @JsonProperty("server") String server,
      @JsonProperty("sourcePlugin") SourcePluginConfig sourcePluginConfig,
      @JsonProperty("user") String user, @JsonProperty("workspace") String workspace) {
    this.apiKey = apiKey;
    this.server = URI.create(server);
    this.sourcePlugin = sourcePluginConfig.sourcePlugin();
    this.pluginArgs = sourcePluginConfig.args();
    this.user = user;
    this.workspace = workspace;
  }

  @Override
  public String apiKey() {
    return apiKey;
  }

  @Override
  public URI server() throws URISyntaxException {
    return server;
  }

  @Override
  public Path sourcePlugin() {
    return sourcePlugin;
  }

  @Override
  public String user() {
    return user;
  }

  @Override
  public String workspace() {
    return workspace;
  }

  @Override
  public String[] pluginArgs() {
    return pluginArgs;
  }

  public static class SourcePluginConfig {
    private final Path sourcePlugin;
    private final String[] args;

    @JsonCreator
    public SourcePluginConfig(@JsonProperty("path") String sourcePlugin,
        @JsonProperty("args") String[] args) {
      this.sourcePlugin = Paths.get(sourcePlugin);
      this.args = args;
    }

    public Path sourcePlugin() {
      return sourcePlugin;
    }

    public String[] args() {
      return args;
    }
  }
}
