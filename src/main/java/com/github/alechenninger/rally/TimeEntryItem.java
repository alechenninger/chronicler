package com.github.alechenninger.rally;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Optional;

public class TimeEntryItem {
  @SerializedName("Project")
  private final Long projectId;

  @SerializedName("WorkProduct")
  private final Long workProductId;

  @SerializedName("Task")
  private final Optional<Long> taskId;

  @SerializedName("User")
  private final String user;

  @SerializedName("WeekStartDate")
  private final Date weekStartDate;

  public TimeEntryItem(Long projectId, Long workProductId, String user, Date weekStartDate) {
    this.projectId = projectId;
    this.workProductId = workProductId;
    this.taskId = Optional.empty();
    this.user = user;
    this.weekStartDate = weekStartDate;
  }

  public TimeEntryItem(Long projectId, Long workProductId, Long taskId, String user,
      Date weekStartDate) {
    this.projectId = projectId;
    this.workProductId = workProductId;
    this.taskId = Optional.of(taskId);
    this.user = user;
    this.weekStartDate = weekStartDate;
  }

  public Long getProjectId() {
    return projectId;
  }

  public Long getWorkProductId() {
    return workProductId;
  }

  public Optional<Long> getTaskId() {
    return taskId;
  }

  public String getUser() {
    return user;
  }

  public Date getWeekStartDate() {
    return weekStartDate;
  }
}
