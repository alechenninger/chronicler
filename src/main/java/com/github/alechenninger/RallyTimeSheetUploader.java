package com.github.alechenninger;

import com.github.alechenninger.rally.TimeEntryItem;
import com.github.alechenninger.rally.TimeEntryValue;

import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Logger;

public class RallyTimeSheetUploader implements TimeSheetUploader {
  private static final Logger logger =  Logger.getLogger(RallyTimeSheetUploader.class.getName());

  private static final String OBJECT_ID = "ObjectID";

  private final RallyRestApi rally;
  private final String user;
  private final String workspaceName;

  public static final DateFormat ISO_8601_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'") {{
    setTimeZone(TimeZone.getTimeZone("UTC"));
  }};

  public RallyTimeSheetUploader(RallyRestApi rally, String user, String workspaceName) {
    this.rally = Objects.requireNonNull(rally, "rally");
    this.user = Objects.requireNonNull(user, "user");
    this.workspaceName = Objects.requireNonNull(workspaceName, "workspaceName");
  }

  @Override
  public void uploadTimeSheet(TimeSheet timeSheet) {
    List<TimeEntry> entries = timeSheet.getEntries();

    try {
      String workspaceRef = workspaceRefByName(workspaceName);

      // TODO: Sort entries by project, work product, and week
      // This means can get project, work product, and time entry item only once per combination
      for (TimeEntry entry : entries) {
        TimeEntryCoordinates coordinates = entry.getCoordinates();
        String projectId = projectIdByName(coordinates.getProject(), workspaceRef);
        String workProductId = workProductIdByName(coordinates.getWorkProduct(), workspaceRef);
        Optional<String> taskId = coordinates.getTask()
            .map((task) -> {
              try {
                return taskIdByName(task, workspaceRef);
              } catch (IOException e) {
                throw new ChroniclerException(e);
              }
            });

        String timeEntryItemId = ensureTimeEntryItem(workspaceRef, projectId, workProductId,
            taskId, entry);

        createTimeEntryValue(timeEntryItemId, entry);
      }
    } catch (IOException e) {
      throw new ChroniclerException(e);
    }
  }

  private void createTimeEntryValue(String timeEntryItemId, TimeEntry entry) throws IOException {
    TimeEntryValue value = new TimeEntryValue(timeEntryItemId, entry.getDay(), entry.getHours());
    CreateRequest newTimeEntryValue = new CreateRequest("timeentryvalue", value.toJson());

    CreateResponse response = rally.create(newTimeEntryValue);

    if (!response.wasSuccessful()) {
      logger.severe("Failed to create time entry value: " + response.getObject());
    }
  }

  private String createTimeEntryItem(String workspaceRef, String projectId, String workProductId,
      Optional<String> taskId, TimeEntry entry) throws IOException {
    TimeEntryItem item;

    if (taskId.isPresent()) {
      item = new TimeEntryItem(projectId, workProductId, taskId.get(), user,
          weekStartDate(entry.getDay()));
    } else {
      item = new TimeEntryItem(projectId, workProductId, user, weekStartDate(entry.getDay()));
    }

    CreateRequest newTimeEntryItem = new CreateRequest("timeentryitem", item.toJson());

    newTimeEntryItem.setFetch(new Fetch(OBJECT_ID));

    CreateResponse response = rally.create(newTimeEntryItem);

    if (!response.wasSuccessful()) {
      throw new ChroniclerException("Failed to create time entry item: " + response.getObject());
    }

    return response.getObject()
        .get(OBJECT_ID)
        .getAsString();
  }

  private Date weekStartDate(Date date) {
    // Would love to just use java.time everywhere but has poor serialization library support
    ZonedDateTime entryDate = date.toInstant().atZone(ZoneId.of("UTC"));
    ZonedDateTime weekStartDate = entryDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    return Date.from(weekStartDate.toInstant());
  }

  /**
   * Gets the {@link com.github.alechenninger.rally.TimeEntryItem} object ID that would align with
   * this {@link com.github.alechenninger.TimeEntry}. If one does not exist, it is created.
   */
  private String ensureTimeEntryItem(String workspaceRef, String projectId,
      String workProductId, Optional<String> taskId, TimeEntry entry) throws IOException {
    return getTimeEntryItem(workspaceRef, projectId, workProductId, taskId, entry)
        .orElse(createTimeEntryItem(workProductId, projectId, workProductId, taskId, entry));
  }

  private Optional<String> getTimeEntryItem(String workspaceRef, String projectId,
      String workProductId, Optional<String> taskId, TimeEntry entry) throws IOException {
    QueryRequest forTimeEntryItem = new QueryRequest("timeentryitem");

    forTimeEntryItem.setWorkspace(workspaceRef);

    QueryFilter byProject = new QueryFilter("Project", "=", projectId);
    QueryFilter byWorkProduct = new QueryFilter("WorkProduct", "=", workProductId);
    QueryFilter byUserName = new QueryFilter("User.UserName", "=", user);
    QueryFilter byWeekStartDate = new QueryFilter("WeekStartDate", "=",
        ISO_8601_UTC.format(weekStartDate(entry.getDay())));

    if (taskId.isPresent()) {
      QueryFilter byTask = new QueryFilter("Task", "=", taskId.get());

      forTimeEntryItem.setQueryFilter(byUserName.and(byWeekStartDate).and(byProject)
          .and(byWorkProduct).and(byTask));
    } else {
      forTimeEntryItem.setQueryFilter(byUserName.and(byWeekStartDate).and(byProject)
          .and(byWorkProduct));
    }

    QueryResponse response = rally.query(forTimeEntryItem);

    if (response.getTotalResultCount() == 0) {
      return Optional.empty();
    }

    if (response.getTotalResultCount() > 1) {
      // TODO
    }

    return Optional.of(response.getResults()
        .get(0)
        .getAsJsonObject()
        .get(OBJECT_ID)
        .getAsString());
  }

  private String taskIdByName(String task, String workspaceRef) throws IOException {
    QueryRequest forTask = new QueryRequest("task");
    QueryFilter byName = new QueryFilter("Name", "=", task);

    forTask.setQueryFilter(byName);
    forTask.setWorkspace(workspaceRef);
    forTask.setFetch(new Fetch(OBJECT_ID));

    QueryResponse result = rally.query(forTask);

    if (result.getTotalResultCount() == 0) {
      throw new ChroniclerException("No tasks found for task name, " + task);
    }

    return result.getResults()
        .get(0)
        .getAsJsonObject()
        .get(OBJECT_ID)
        .getAsString();
  }

  private String workProductIdByName(String workProduct, String workspaceRef) throws IOException {
    QueryRequest forWorkProduct = new QueryRequest("artifact");
    QueryFilter byName = new QueryFilter("Name", "=", workProduct);

    forWorkProduct.setQueryFilter(byName);
    forWorkProduct.setWorkspace(workspaceRef);
    forWorkProduct.setFetch(new Fetch(OBJECT_ID));

    QueryResponse result = rally.query(forWorkProduct);

    if (result.getTotalResultCount() == 0) {
      throw new ChroniclerException("No work products found for task name, " + workProduct);
    }

    return result.getResults()
        .get(0)
        .getAsJsonObject()
        .get(OBJECT_ID)
        .getAsString();
  }

  private String projectIdByName(String project, String workspaceRef) throws IOException {
    QueryRequest forProject = new QueryRequest("project");
    QueryFilter byName = new QueryFilter("Name", "=", project);

    forProject.setQueryFilter(byName);
    forProject.setWorkspace(workspaceRef);
    forProject.setFetch(new Fetch(OBJECT_ID));

    QueryResponse result = rally.query(forProject);

    if (result.getTotalResultCount() == 0) {
      throw new ChroniclerException("No projects found for name, " + project);
    }

    return result.getResults()
        .get(0)
        .getAsJsonObject()
        .get(OBJECT_ID)
        .getAsString();
  }

  private String workspaceRefByName(String workspace) throws IOException {
    String subscriptionId = firstSubscriptionId();

    QueryRequest forWorkspace = new QueryRequest("subscription/" + subscriptionId + "/workspaces");
    QueryFilter byName = new QueryFilter("Name", "=", workspace);

    forWorkspace.setQueryFilter(byName);
    forWorkspace.setFetch(new Fetch("_ref"));

    QueryResponse response = rally.query(forWorkspace);

    if (response.getTotalResultCount() == 0) {
      throw new ChroniclerException("No workspaces found for subscription, " + subscriptionId + ", "
          + "with name, " + workspace + ".");
    }

    if (response.getTotalResultCount() > 1) {
      throw new ChroniclerException("Multiple workspaces found for subscription, " + subscriptionId
          + ", with name, " + workspace + ".");
    }

    return response.getResults()
        .get(0)
        .getAsJsonObject()
        .get("_ref")
        .getAsString();
  }

  private String firstSubscriptionId() throws IOException {
    QueryRequest subscription = new QueryRequest("subscription");
    subscription.setFetch(new Fetch(OBJECT_ID));
    QueryResponse subscriptionResult = rally.query(subscription);

    if (subscriptionResult.getTotalResultCount() == 0) {
      throw new ChroniclerException("Account has no subscriptions?");
    }

    if (subscriptionResult.getTotalResultCount() > 1) {
      throw new ChroniclerException("Got more than one subscription associated with account. "
          + "Provide a subscription name.");
    }

    return subscriptionResult.getResults()
        .get(0)
        .getAsJsonObject()
        .get(OBJECT_ID)
        .getAsString();
  }
}
