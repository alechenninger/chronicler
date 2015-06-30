package com.github.alechenninger.chronicler;

import java.time.ZonedDateTime;

public class TimeEntry {
  private final TimeEntryCoordinates coordinates;
  private final ZonedDateTime day;
  private final Float hours;

  public TimeEntry(TimeEntryCoordinates coordinates, ZonedDateTime day, Float hours) {
    this.coordinates = coordinates;
    this.day = day;
    this.hours = hours;
  }

  public TimeEntryCoordinates getCoordinates() {
    return coordinates;
  }

  public ZonedDateTime getDay() {
    return day;
  }

  public Float getHours() {
    return hours;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TimeEntry timeEntry = (TimeEntry) o;

    if (!coordinates.equals(timeEntry.coordinates)) {
      return false;
    }
    if (!day.equals(timeEntry.day)) {
      return false;
    }
    if (!hours.equals(timeEntry.hours)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = coordinates.hashCode();
    result = 31 * result + day.hashCode();
    result = 31 * result + hours.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "TimeEntry{" +
        "coordinates=" + coordinates +
        ", day=" + day +
        ", hours=" + hours +
        '}';
  }
}
