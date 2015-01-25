package com.github.alechenninger;

import com.github.alechenninger.hamster.HamsterTimeSheetFactory;

import java.nio.file.Path;

public interface TimeSheetFactory {
  public static TimeSheetFactory byType(String type) {
    type = type.toLowerCase();

    // TODO: Some kind of service loader thing or something
    if ("hamsterxml".equals(type)) {
      return new HamsterTimeSheetFactory();
    }

    throw new HamsterRallyException("Unsupported time sheet type, " + type);
  }

  TimeSheet parseTimeSheet(String[] additionalArgs);
}
