package com.github.alechenninger.chronicler.rally;

import com.github.alechenninger.chronicler.ChroniclerException;

import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;

class TimeEntryValue {
  private final String timeEntryItemId;
  private final Date date;
  private final BigDecimal hours;

  public TimeEntryValue(String timeEntryItemId, Date date, Float hours) {
    this(timeEntryItemId, date, new BigDecimal(String.valueOf(hours)));
  }

  public TimeEntryValue(String timeEntryItemId, Date date, BigDecimal hours) {
    this.timeEntryItemId = timeEntryItemId;
    this.date = date;
    this.hours = hours;
  }

  public String getTimeEntryItemId() {
    return timeEntryItemId;
  }

  public Date getDateVal() {
    return date;
  }

  public BigDecimal getHours() {
    return hours.setScale(2, RoundingMode.HALF_UP);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("TimeEntryItem", "/timeentryitem/" + getTimeEntryItemId());
    json.addProperty("DateVal", RallyExternalTimeSheet.ISO_8601_UTC.format(getDateVal()));
    json.addProperty("Hours", getHours());
    return json;
  }

  public static TimeEntryValue fromJson(JsonObject json) throws ParseException {
    String timeEntryItemId = json.get("TimeEntryItem").getAsString().replaceAll("/.*/", "");
    Date date = RallyExternalTimeSheet.ISO_8601_UTC.parse(json.get("DateVal").getAsString());
    BigDecimal hours = new BigDecimal(json.get("Hours").getAsString());
    return new TimeEntryValue(timeEntryItemId, date, hours);
  }

  public static TimeEntryValue fromJsonQuietly(JsonObject json) {
    try {
      return fromJson(json);
    } catch (ParseException e) {
      throw new ChroniclerException(e);
    }
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
