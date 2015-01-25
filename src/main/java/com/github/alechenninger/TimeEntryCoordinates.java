package com.github.alechenninger;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Optional;

public class TimeEntryCoordinates {
  private final String project;
  private final String workProduct;
  private final Optional<String> task;

  @JsonCreator
  public TimeEntryCoordinates(String project, String workProduct) {
    this.project = project;
    this.workProduct = workProduct;
    this.task = Optional.empty();
  }

  @JsonCreator
  public TimeEntryCoordinates(String project, String workProduct, String task) {
    this.project = project;
    this.workProduct = workProduct;
    this.task = Optional.of(task);
  }

  public String getProject() {
    return project;
  }

  public String getWorkProduct() {
    return workProduct;
  }

  public Optional<String> getTask() {
    return task;
  }
}
