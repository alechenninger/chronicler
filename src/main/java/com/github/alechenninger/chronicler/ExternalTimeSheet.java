package com.github.alechenninger.chronicler;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface ExternalTimeSheet {
  Optional<ZonedDateTime> getLastRecordedEntryTime();
  void uploadTimeSheet(TimeSheet timeSheet);
}
