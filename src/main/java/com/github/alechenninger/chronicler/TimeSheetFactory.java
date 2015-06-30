package com.github.alechenninger.chronicler;

import java.time.ZonedDateTime;

public interface TimeSheetFactory {
  TimeSheet getTimeSheet(String[] additionalArgs);

  default TimeSheet getTimeSheet(String[] additionalArgs, ZonedDateTime lastRecordedEntryTime) {
    return getTimeSheet(additionalArgs);
  }
}
