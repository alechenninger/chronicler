package com.github.alechenninger.chronicler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfigFactory {
  private static final ObjectMapper mapper = new ObjectMapper();

  public ChroniclerConfig fromCommandLine(String[] args) throws ParseException, IOException {
    CmdLineChroniclerConfig cmdLineConfig = new CmdLineChroniclerConfig(args);
    File jsonConfig = cmdLineConfig.config().toFile();

    if (jsonConfig.exists()) {
      return new OverriddenChroniclerConfig(fromJson(jsonConfig), cmdLineConfig);
    }

    return cmdLineConfig;
  }

  public ChroniclerConfig fromJson(File json) throws IOException {
    if (json.exists()) {
      return mapper.readValue(json, DefaultChroniclerConfig.class);
    }

    throw new FileNotFoundException(json.toString());
  }
}
