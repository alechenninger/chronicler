package com.github.alechenninger.hamster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.alechenninger.TimeEntry;

public class ActivityMapping {
  private final String category;
  private final TimeEntry coordinates;

  @JsonCreator
  public ActivityMapping(String category, TimeEntry coordinates) {
    this.category = category;
    this.coordinates = coordinates;
  }
}
