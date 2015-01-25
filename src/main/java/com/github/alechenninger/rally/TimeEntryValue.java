package com.github.alechenninger.rally;

import java.util.Date;

public class TimeEntryValue {
  private final Long timeEntryItemId;
  private final Date date;
  private final Float hours;

  public TimeEntryValue(Long timeEntryItemId, Date date, Float hours) {
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

  public Float getHours() {
    return hours;
  }
}
