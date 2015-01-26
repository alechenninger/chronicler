package com.github.alechenninger.rally;

import com.github.alechenninger.RallyTimeSheetUploader;

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
}
