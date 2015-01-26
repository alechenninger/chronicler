package com.github.alechenninger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class TimeEntryCoordinates {
  private final String project;
  private final String workProduct;
  private final Optional<String> task;

  public TimeEntryCoordinates(String project, String workProduct) {
    this.project = project;
    this.workProduct = workProduct;
    this.task = Optional.empty();
  }

  @JsonCreator
  public TimeEntryCoordinates(@JsonProperty("project") String project,
      @JsonProperty("workProduct") String workProduct, @JsonProperty("task") String task) {
    this.project = project;
    this.workProduct = workProduct;
    this.task = Optional.ofNullable(task);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TimeEntryCoordinates that = (TimeEntryCoordinates) o;

    if (!project.equals(that.project)) {
      return false;
    }
    if (!task.equals(that.task)) {
      return false;
    }
    if (!workProduct.equals(that.workProduct)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = project.hashCode();
    result = 31 * result + workProduct.hashCode();
    result = 31 * result + task.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "TimeEntryCoordinates{" +
        "project='" + project + '\'' +
        ", workProduct='" + workProduct + '\'' +
        ", task=" + task +
        '}';
  }
}
