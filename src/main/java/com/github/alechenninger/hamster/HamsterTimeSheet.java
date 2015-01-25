package com.github.alechenninger.hamster;

import com.github.alechenninger.TimeEntry;
import com.github.alechenninger.TimeEntryCoordinates;
import com.github.alechenninger.TimeSheet;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HamsterTimeSheet implements TimeSheet {
  private final List<Activity> activities;
  private final Map<String, TimeEntryCoordinates> categoryMap;

  public HamsterTimeSheet(List<Activity> activities,
      Map<String, TimeEntryCoordinates> categoryMap) {
    this.activities = Objects.requireNonNull(activities, "activites");
    this.categoryMap = Objects.requireNonNull(categoryMap, "categoryMap");
  }

  @Override
  public List<TimeEntry> getEntries() {
    return activities.stream()
        .map(a -> new TimeEntry(categoryMap.get(a.getCategory()), a.getStartTime(),
            (float) a.getDurationInMinutes() / 60f))
        .collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HamsterTimeSheet that = (HamsterTimeSheet) o;

    if (!activities.equals(that.activities)) {
      return false;
    }
    if (!categoryMap.equals(that.categoryMap)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = activities.hashCode();
    result = 31 * result + categoryMap.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "HamsterTimeSheet{" +
        "activities=" + activities +
        ", categoryMap=" + categoryMap +
        '}';
  }
}
