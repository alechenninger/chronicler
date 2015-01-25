package com.github.alechenninger;

import java.util.Date;

public class TimeEntry {
  private final TimeEntryCoordinates coordinates;
  private final Date day;
  private final float hours;

  public TimeEntry(TimeEntryCoordinates coordinates, Date day, float hours) {
    this.coordinates = coordinates;
    this.day = day;
    this.hours = hours;
  }

  public TimeEntryCoordinates getCoordinates() {
    return coordinates;
  }

  public Date getDay() {
    return day;
  }

  public float getHours() {
    return hours;
  }
}
