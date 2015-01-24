package com.github.alechenninger.hamster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Activity {
  private final String category;
  private final String description;
  private final String name;
  private final String tags;
  private final int durationInMinutes;
  private final Date startTime;
  private final Date endTime;

  @JsonCreator
  public Activity(@JsonProperty("category") String category,
      @JsonProperty("description") String description, @JsonProperty("name") String name,
      @JsonProperty("tags") String tags,
      @JsonProperty("duration_minutes") int durationInMinutes,
      @JsonProperty("end_time")
      @JsonFormat(shape =JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
      @JsonProperty("start_time")
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
      Date startTime) {
    this.category = category;
    this.description = description;
    this.name = name;
    this.tags = tags;
    this.durationInMinutes = durationInMinutes;
    this.endTime = endTime;
    this.startTime = startTime;
  }

  public String getCategory() {
    return category;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public String getTags() {
    return tags;
  }

  public int getDurationInMinutes() {
    return durationInMinutes;
  }

  public Date getStartTime() {
    return startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Activity activity = (Activity) o;

    if (durationInMinutes != activity.durationInMinutes) {
      return false;
    }
    if (category != null ? !category.equals(activity.category) : activity.category != null) {
      return false;
    }
    if (description != null ? !description.equals(activity.description) : activity.description !=
        null) {
      return false;
    }
    if (endTime != null ? !endTime.equals(activity.endTime) : activity.endTime != null) {
      return false;
    }
    if (name != null ? !name.equals(activity.name) : activity.name != null) {
      return false;
    }
    if (startTime != null ? !startTime.equals(activity.startTime) : activity.startTime != null) {
      return false;
    }
    if (tags != null ? !tags.equals(activity.tags) : activity.tags != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = category != null ? category.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (tags != null ? tags.hashCode() : 0);
    result = 31 * result + durationInMinutes;
    result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
    result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Activity{" +
        "category='" + category + '\'' +
        ", description='" + description + '\'' +
        ", name='" + name + '\'' +
        ", tags='" + tags + '\'' +
        ", durationInMinutes=" + durationInMinutes +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        '}';
  }
}
