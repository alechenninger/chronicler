package com.github.alechenninger.chronicler.rally;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(JUnit4.class)
public class TimeEntryValueTest {
  @Test
  public void shouldRoundHoursToTwoDecimalPlaces() {
    TimeEntryValue value = new TimeEntryValue("1", new Date(), 10.1234f);
    assertEquals(new BigDecimal("10.12"), value.getHours());
  }

  @Test
  public void shouldRoundHoursToTwoDecimalPlacesRoundingUpAtHalfWay() {
    TimeEntryValue value = new TimeEntryValue("1", new Date(), 10.125f);
    assertEquals(new BigDecimal("10.13"), value.getHours());
  }

  @Test
  public void shouldRoundHoursToTwoDecimalPlacesInJson() {
    TimeEntryValue value = new TimeEntryValue("1", new Date(), 10.125f);
    JsonObject json = value.toJson();
    assertEquals(new JsonPrimitive(new BigDecimal("10.13")), json.get("Hours"));
  }
}
