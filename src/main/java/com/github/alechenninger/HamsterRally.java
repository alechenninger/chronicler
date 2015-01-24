package com.github.alechenninger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.alechenninger.hamster.Activity;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class HamsterRally {
  private static final ObjectMapper mapper = new XmlMapper()
      .registerModule(new ParameterNamesModule());

  public static void main(String[] args) throws Exception {
    HamsterRallyOptions options = new HamsterRallyOptions(args);

    if (options.helpRequested()) {
      HamsterRallyOptions.printHelpMessage();
      return;
    }

    File report = Paths.get("report.xml").toFile();

    List<Activity> activities = mapper.readValue(report, new TypeReference<List<Activity>>() {});

    System.out.println(activities);
  }
}
