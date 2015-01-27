package com.github.alechenninger.chronicler.rally;

import com.github.alechenninger.chronicler.RallyTimeSheetUploader;

import com.google.gson.JsonObject;

import java.util.Date;

public class TimeEntryValue {
  private final String timeEntryItemId;
  private final Date date;
  private final Float hours;

  public TimeEntryValue(String timeEntryItemId, Date date, Float hours) {
    this.timeEntryItemId = timeEntryItemId;
    this.date = date;
    this.hours = hours;
  }

  public String getTimeEntryItem() {
    return timeEntryItemId;
  }

  public Date getDateVal() {
    return date;
  }

  public Float getHours() {
    return hours;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("TimeEntryItem", "/timeentryitem/" + timeEntryItemId);
    json.addProperty("DateVal", RallyTimeSheetUploader.ISO_8601_UTC.format(date));
    json.addProperty("Hours", hours);
    return json;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TimeEntryValue that = (TimeEntryValue) o;

    if (!date.equals(that.date)) {
      return false;
    }
    if (!hours.equals(that.hours)) {
      return false;
    }
    if (!timeEntryItemId.equals(that.timeEntryItemId)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = timeEntryItemId.hashCode();
    result = 31 * result + date.hashCode();
    result = 31 * result + hours.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "TimeEntryValue{" +
        "timeEntryItemId='" + timeEntryItemId + '\'' +
        ", date=" + date +
        ", hours=" + hours +
        '}';
  }
}
