package com.github.alechenninger;

import java.util.Optional;

public class TimeEntry {
  private final String projectName;
  private final String workProductName;
  private final Optional<String> taskName;

  public TimeEntry(String projectName, String workProductName) {
    this.projectName = projectName;
    this.workProductName = workProductName;
    this.taskName = Optional.empty();
  }

  public TimeEntry(String projectName, String workProductName, String taskName) {
    this.projectName = projectName;
    this.workProductName = workProductName;
    this.taskName = Optional.of(taskName);
  }

  public String getProjectName() {
    return projectName;
  }

  public String getWorkProductName() {
    return workProductName;
  }

  public Optional<String> getTaskName() {
    return taskName;
  }
}
