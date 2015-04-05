package com.github.alechenninger.chronicler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SerializableChroniclerOptions implements ChroniclerOptions {
  private final String apiKey;
  private final URI server;
  private final Path sourcePlugin;
  private final String user;
  private final String workspace;

  @JsonCreator
  public SerializableChroniclerOptions(@JsonProperty("apiKey") String apiKey,
      @JsonProperty("server") String server, @JsonProperty("sourcePlugin") String sourcePlugin,
      @JsonProperty("user") String user, @JsonProperty("workspace") String workspace) {
    this.apiKey = apiKey;
    this.server = URI.create(server);
    this.sourcePlugin = Paths.get(sourcePlugin);
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
}
