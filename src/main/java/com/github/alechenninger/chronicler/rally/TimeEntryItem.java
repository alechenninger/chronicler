package com.github.alechenninger.chronicler.rally;

import com.github.alechenninger.chronicler.RallyTimeSheetUploader;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Optional;

public class TimeEntryItem {
  private final String projectId;
  private final String workProductId;
  private final Optional<String> taskId;
  private final String user;
  private final Date weekStartDate;

  public TimeEntryItem(String projectId, String workProductId, String user, Date weekStartDate) {
    this.projectId = projectId;
    this.workProductId = workProductId;
    this.taskId = Optional.empty();
    this.user = user;
    this.weekStartDate = weekStartDate;
  }

  public TimeEntryItem(String projectId, String workProductId, String taskId, String user,
      Date weekStartDate) {
    this.projectId = projectId;
    this.workProductId = workProductId;
    this.taskId = Optional.of(taskId);
    this.user = user;
    this.weekStartDate = weekStartDate;
  }

  public String getProjectId() {
    return projectId;
  }

  public String getWorkProductId() {
    return workProductId;
  }

  public Optional<String> getTaskId() {
    return taskId;
  }

  public String getUser() {
    return user;
  }

  public Date getWeekStartDate() {
    return weekStartDate;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("Project", projectId);
    json.addProperty("WorkProduct", workProductId);
    taskId.ifPresent(t -> json.addProperty("Task", t));
    json.addProperty("WeekStartDate", RallyTimeSheetUploader.ISO_8601_UTC.format(weekStartDate));
    json.addProperty("User", user);
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

    TimeEntryItem that = (TimeEntryItem) o;

    if (!projectId.equals(that.projectId)) {
      return false;
    }
    if (!taskId.equals(that.taskId)) {
      return false;
    }
    if (!user.equals(that.user)) {
      return false;
    }
    if (!weekStartDate.equals(that.weekStartDate)) {
      return false;
    }
    if (!workProductId.equals(that.workProductId)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = projectId.hashCode();
    result = 31 * result + workProductId.hashCode();
    result = 31 * result + taskId.hashCode();
    result = 31 * result + user.hashCode();
    result = 31 * result + weekStartDate.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "TimeEntryItem{" +
        "projectId='" + projectId + '\'' +
        ", workProductId='" + workProductId + '\'' +
        ", taskId=" + taskId +
        ", user='" + user + '\'' +
        ", weekStartDate=" + weekStartDate +
        '}';
  }
}
