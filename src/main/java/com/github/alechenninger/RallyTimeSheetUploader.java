package com.github.alechenninger;

import com.github.alechenninger.gson.OptionalTypeAdapterFactory;
import com.github.alechenninger.rally.TimeEntryItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RallyTimeSheetUploader implements TimeSheetUploader {
  private static final Gson gson = new GsonBuilder()
      .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
      .create();

  private final RallyRestApi rally;
  private final String user;

  public RallyTimeSheetUploader(RallyRestApi rally, String user) {
    this.rally = Objects.requireNonNull(rally, "rally");
    this.user = Objects.requireNonNull(user, "user");
  }

  @Override
  public void uploadTimeSheet(TimeSheet timeSheet) {
    List<TimeEntry> entries = timeSheet.getEntries();

    try {
      for (TimeEntry entry : entries) {
        TimeEntryCoordinates coordinates = entry.getCoordinates();

        String projectId = projectIdByName(coordinates.getProject());
        String workProductId = workProductIdByName(coordinates.getWorkProduct());
        Optional<String> taskId = coordinates.getTask()
            .map((task) -> {
              try {
                return taskIdByName(task);
              } catch (IOException e) {
                throw new HamsterRallyException(e);
              }
            });

        if (!hasTimeEntryItem(projectId, workProductId, taskId)) {
          createTimeEntryItem(entry);
        }

        createTimeEntryValue(entry);
      }
    } catch (IOException e) {
      throw new HamsterRallyException(e);
    }
  }

  private void createTimeEntryItem(String projectId, String workProductId, Optional<String> taskId,
      TimeEntry entry) {
    TimeEntryItem item;

    if (taskId.isPresent()) {
      item = new TimeEntryItem(projectId, workProductId, taskId, user, weekStartDate(entry.getDay()));
    }

    CreateRequest createTimeEntryItem = new CreateRequest("")
  }

  private Date weekStartDate(Date day) {
    return null;
  }

  private boolean hasTimeEntryItem(String projectId, String workProductId, Optional<String> taskId)
      throws IOException {
    QueryRequest timeEntryItems = new QueryRequest("timeentryitem");

    QueryFilter byProject = new QueryFilter("Project", "=", projectId);
    QueryFilter byWorkProduct = new QueryFilter("WorkProduct", "=", workProductId);

    if (taskId.isPresent()) {
      QueryFilter byTask = new QueryFilter("Task", "=", taskId.get());

      timeEntryItems.setQueryFilter(byProject.and(byWorkProduct).and(byTask));
    } else {
      timeEntryItems.setQueryFilter(byProject.and(byWorkProduct));
    }

    return rally.query(timeEntryItems).getTotalResultCount() > 0;
  }

  private String taskIdByName(String task) throws IOException {
    QueryRequest taskId = new QueryRequest("task");
    QueryFilter taskName = new QueryFilter("Name", "=", task);

    taskId.setQueryFilter(taskName);

    QueryResponse result = rally.query(taskId);

    if (result.getTotalResultCount() == 0) {
      throw new HamsterRallyException("No tasks found for task name, " + task);
    }

    return result.getResults()
        .get(0)
        .getAsJsonObject()
        .get("ObjectId")
        .getAsString();
  }

  private String workProductIdByName(String workProduct) throws IOException {
    QueryRequest taskId = new QueryRequest("workproduct");
    QueryFilter taskName = new QueryFilter("Name", "=", workProduct);

    taskId.setQueryFilter(taskName);

    QueryResponse result = rally.query(taskId);

    if (result.getTotalResultCount() == 0) {
      throw new HamsterRallyException("No tasks found for task name, " + workProduct);
    }

    return result.getResults()
        .get(0)
        .getAsJsonObject()
        .get("ObjectId")
        .getAsString();
  }

  private String projectIdByName(String project) throws IOException {
    QueryRequest taskId = new QueryRequest("project");
    QueryFilter taskName = new QueryFilter("Name", "=", project);

    taskId.setQueryFilter(taskName);

    QueryResponse result = rally.query(taskId);

    if (result.getTotalResultCount() == 0) {
      throw new HamsterRallyException("No tasks found for task name, " + project);
    }

    return result.getResults()
        .get(0)
        .getAsJsonObject()
        .get("ObjectId")
        .getAsString();
  }
}
