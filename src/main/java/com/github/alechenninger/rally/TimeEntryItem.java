package com.github.alechenninger.rally;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

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

  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'") {{
    setTimeZone(UTC);
  }};

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

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("Project", projectId);
    json.addProperty("WorkProduct", workProductId);
    taskId.ifPresent(t -> json.addProperty("Task", t));
    json.addProperty("WeekStartDate", DATE_FORMAT.format(weekStartDate));
    json.addProperty("User", user);
    return json;
  }
}
