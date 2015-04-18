package com.github.alechenninger.chronicler.config;

import com.github.alechenninger.chronicler.ChroniclerException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.function.Function;

public class OverriddenChroniclerConfig implements ChroniclerConfig {
  private final ChroniclerConfig original;
  private final ChroniclerConfig override;

  public OverriddenChroniclerConfig(ChroniclerConfig original, ChroniclerConfig override) {
    this.original = original;
    this.override = override;
  }

  @Override
  public String apiKey() {
    return getOverrideOrOriginal(ChroniclerConfig::apiKey);
  }

  @Override
  public URI server() throws URISyntaxException {
    try {
      return override.server();
    } catch (ChroniclerException e) {
      return original.server();
    }
  }

  @Override
  public Path sourcePlugin() {
    return getOverrideOrOriginal(ChroniclerConfig::sourcePlugin);
  }

  @Override
  public String user() {
    return getOverrideOrOriginal(ChroniclerConfig::user);
  }

  @Override
  public String workspace() {
    return getOverrideOrOriginal(ChroniclerConfig::workspace);
  }

  @Override
  public String[] pluginArgs() {
    String[] originalArgs = original.pluginArgs();
    String[] overrideArgs = override.pluginArgs();
    String[] mergedArgs = new String[originalArgs.length + overrideArgs.length];
    System.arraycopy(originalArgs, 0, mergedArgs, 0, originalArgs.length);
    System.arraycopy(overrideArgs, 0, mergedArgs, originalArgs.length, overrideArgs.length);
    return mergedArgs;
  }

  private <T> T getOverrideOrOriginal(Function<ChroniclerConfig, T> property) {
    try {
      return property.apply(override);
    } catch (ChroniclerException e) {
      return property.apply(original);
    }
  }
}
