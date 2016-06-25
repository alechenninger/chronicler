package com.github.alechenninger.chronicler;

import java.time.ZonedDateTime;

public interface TimeSheetFactory {
  TimeSheet getTimeSheet(String[] args);

  default TimeSheet getTimeSheet(String[] args, ZonedDateTime lastRecordedEntryTime) {
    return getTimeSheet(args);
  }
}
