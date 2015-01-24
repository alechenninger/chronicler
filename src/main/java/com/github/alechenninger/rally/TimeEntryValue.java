package com.github.alechenninger.rally;

import java.util.Date;

public class TimeEntryValue {
  private final Long timeEntryItemId;
  private final Date date;
  private final Long hours;

  public TimeEntryValue(Long timeEntryItemId, Date date, Long hours) {
    this.timeEntryItemId = timeEntryItemId;
    this.date = date;
    this.hours = hours;
  }

  public Long getTimeEntryItem() {
    return timeEntryItemId;
  }

  public Date getDateVal() {
    return date;
  }

  public Long getHours() {
    return hours;
  }
}
