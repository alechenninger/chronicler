package com.github.alechenninger.hamster;

import com.github.alechenninger.ChroniclerException;
import com.github.alechenninger.TimeEntryCoordinates;
import com.github.alechenninger.TimeSheet;
import com.github.alechenninger.TimeSheetFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class HamsterTimeSheetFactory implements TimeSheetFactory {
  private static final ObjectMapper xmlMapper = new XmlMapper();

  private static final ObjectMapper jsonMapper = new ObjectMapper();

  @Override
  public TimeSheet parseTimeSheet(String[] additionalArgs) {
    try {
      HamsterTimeSheetOptions options = new HamsterTimeSheetOptions(additionalArgs);
      Path categoryMapPath = options.categoryMap();
      Path report = options.report();

      List<Activity> activities = xmlMapper
          .readValue(report.toFile(), new TypeReference<List<Activity>>() {});
      Map<String, TimeEntryCoordinates> categoryMap = jsonMapper
          .readValue(categoryMapPath.toFile(),
              new TypeReference<Map<String, TimeEntryCoordinates>>() {});

      return new HamsterTimeSheet(activities, categoryMap);
    } catch (IOException | ParseException e) {
      throw new ChroniclerException(e);
    }
  }
}
