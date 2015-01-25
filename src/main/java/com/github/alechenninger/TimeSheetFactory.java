package com.github.alechenninger;

import com.github.alechenninger.hamster.HamsterTimeSheetFactory;

public interface TimeSheetFactory {
  public static TimeSheetFactory byType(String type) {
    type = type.toLowerCase();

    // TODO: Some kind of service loader thing or something
    if ("hamsterxml".equals(type)) {
      return new HamsterTimeSheetFactory();
    }

    throw new ChroniclerException("Unsupported time sheet type, " + type);
  }

  TimeSheet parseTimeSheet(String[] additionalArgs);
}
