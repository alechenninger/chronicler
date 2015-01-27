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
}
