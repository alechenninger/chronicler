package com.github.alechenninger.chronicler;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TimeEntry timeEntry = (TimeEntry) o;

    if (Float.compare(timeEntry.hours, hours) != 0) {
      return false;
    }
    if (!coordinates.equals(timeEntry.coordinates)) {
      return false;
    }
    if (!day.equals(timeEntry.day)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = coordinates.hashCode();
    result = 31 * result + day.hashCode();
    result = 31 * result + (hours != +0.0f ? Float.floatToIntBits(hours) : 0);
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
