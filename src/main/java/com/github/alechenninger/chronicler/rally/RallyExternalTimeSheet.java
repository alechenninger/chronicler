package com.github.alechenninger.chronicler.rally;

import com.github.alechenninger.chronicler.ChroniclerException;
import com.github.alechenninger.chronicler.TimeEntry;
import com.github.alechenninger.chronicler.TimeEntryCoordinates;
import com.github.alechenninger.chronicler.TimeSheet;
import com.github.alechenninger.chronicler.ExternalTimeSheet;
import com.github.alechenninger.chronicler.console.Exit;
import com.github.alechenninger.chronicler.console.Prompter;

import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RallyExternalTimeSheet implements ExternalTimeSheet {
  private static final Logger logger =  Logger.getLogger(RallyExternalTimeSheet.class.getName());

  private static final String OBJECT_ID = "ObjectID";

  private final RallyRestApi rally;
  private final String user;
  private final String workspaceName;
  private final Prompter prompter;
  private final Exit exit;

  public static final DateTimeFormatter ISO_8601_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .withZone(ZoneId.of("UTC"));

  private static RallyRestApi getRallyRestApi(URI server, String apiKey) {
    RallyRestApi rally = new RallyRestApi(server, apiKey);
    rally.setWsapiVersion("v2.0");

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        rally.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));

    return rally;
  }

  public RallyExternalTimeSheet(URI server, String apiKey, String user, String workspaceName,
      Prompter prompter, Exit exit) {
    this(getRallyRestApi(server, apiKey), user, workspaceName, prompter, exit);
  }

  public RallyExternalTimeSheet(RallyRestApi rally, String user, String workspaceName,
      Prompter prompter, Exit exit) {
    this.rally = Objects.requireNonNull(rally, "rally");
    this.user = Objects.requireNonNull(user, "user");
    this.workspaceName = Objects.requireNonNull(workspaceName, "workspaceName");
    this.prompter = Objects.requireNonNull(prompter, "prompter");
    this.exit = Objects.requireNonNull(exit, "prompter");
  }

  @Override
  public Optional<ZonedDateTime> getLastRecordedEntryTime() {
    try {
      QueryRequest forMostRecentValue = new QueryRequest("timeentryvalue");
      QueryFilter byUser = new QueryFilter("TimeEntryItem.User", "=", user);
      String workspaceRef = workspaceRefByName(workspaceName);

      forMostRecentValue.setWorkspace(workspaceRef);
      forMostRecentValue.setOrder("DateVal desc");
      forMostRecentValue.setLimit(1);
      forMostRecentValue.setQueryFilter(byUser);
      forMostRecentValue.setPageSize(1);

      QueryResponse response = rally.query(forMostRecentValue);

      if (!response.wasSuccessful()) {
        throw new ChroniclerException("Failed to find most recent time entry value: "
            + Arrays.toString(response.getErrors()));
      }

      if (response.getTotalResultCount() == 0) {
        return Optional.empty();
      }

      TimeEntryValue value = TimeEntryValue.fromJson(response.getResults()
          .get(0)
          .getAsJsonObject());

      // Move to system time zone since Rally's precision is really only the day here.
      return Optional.of(value.getDateVal().withZoneSameLocal(ZoneId.systemDefault()));
    } catch (IOException | ParseException e) {
      throw new ChroniclerException(e);
    }
  }

  @Override
  public void uploadTimeSheet(TimeSheet timeSheet) {
    List<TimeEntry> entries = consolidateEntries(timeSheet.getEntries());

    try {
      String workspaceRef = workspaceRefByName(workspaceName);

      // TODO: Sort entries by project, work product, and week
      // This means can get project, work product, and time entry item only once per combination
      for (TimeEntry entry : entries) {
        TimeEntryCoordinates coordinates = entry.getCoordinates();

        String projectId = projectIdByName(coordinates.getProject(), workspaceRef);
        String workProductId = workProductIdByNameAndProject(coordinates.getWorkProduct(),
            projectId, workspaceRef);
        Optional<String> taskId = coordinates.getTask()
            .map((task) -> {
              try {
                return taskIdByNameAndWorkProduct(task, workProductId, workspaceRef);
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

    if (response.wasSuccessful()) {
      logger.info("Successfully created time entry value, " + value);
    } else {
      logger.severe("Failed to create time entry value, " + value + ", for entry, " + entry + ": "
          + Arrays.toString(response.getErrors()));
    }
  }

  /**
   * Gets the {@link com.github.alechenninger.chronicler.rally.TimeEntryItem} object ID that would
   * align with this {@link TimeEntry}. If one does not exist, it is created.
   */
  private String ensureTimeEntryItem(String workspaceRef, String projectId,
      String workProductId, Optional<String> taskId, TimeEntry entry) throws IOException {
    Optional<String> itemId = getTimeEntryItem(workspaceRef, projectId, workProductId, taskId,
        entry);

    if (itemId.isPresent()) {
      return itemId.get();
    }

    return createTimeEntryItem(projectId, workProductId, taskId, entry);
  }

  private String createTimeEntryItem(String projectId, String workProductId,
      Optional<String> taskId, TimeEntry entry) throws IOException {
    ZonedDateTime weekStartDate = weekStartDateUtc(entry.getDay());

    TimeEntryItem item = taskId
        .map(_taskId -> new TimeEntryItem(projectId, workProductId, _taskId, user, weekStartDate))
        .orElse(new TimeEntryItem(projectId, workProductId, user, weekStartDate));

    CreateRequest newTimeEntryItem = new CreateRequest("timeentryitem", item.toJson());
    newTimeEntryItem.setFetch(new Fetch(OBJECT_ID));

    CreateResponse response = rally.create(newTimeEntryItem);

    if (!response.wasSuccessful()) {
      throw new ChroniclerException("Failed to create time entry item: "
          + Arrays.toString(response.getErrors()));
    }

    return response.getObject()
        .get(OBJECT_ID)
        .getAsString();
  }

  private Optional<String> getTimeEntryItem(String workspaceRef, String projectId,
      String workProductId, Optional<String> taskId, TimeEntry entry) throws IOException {
    QueryRequest forTimeEntryItem = new QueryRequest("timeentryitem");

    forTimeEntryItem.setWorkspace(workspaceRef);

    QueryFilter byProject = new QueryFilter("Project.ObjectID", "=", projectId);
    QueryFilter byWorkProduct = new QueryFilter("WorkProduct.ObjectID", "=", workProductId);
    QueryFilter byUserName = new QueryFilter("User.UserName", "=", user);
    QueryFilter byWeekStartDate = new QueryFilter("WeekStartDate", "=",
        ISO_8601_UTC.format(weekStartDateUtc(entry.getDay())));

    if (taskId.isPresent()) {
      QueryFilter byTask = new QueryFilter("Task.ObjectID", "=", taskId.get());

      forTimeEntryItem.setQueryFilter(byUserName.and(byWeekStartDate).and(byProject)
          .and(byWorkProduct).and(byTask));
    } else {
      forTimeEntryItem.setQueryFilter(byUserName.and(byWeekStartDate).and(byProject)
          .and(byWorkProduct));
    }

    QueryResponse response = rally.query(forTimeEntryItem);

    if (!response.wasSuccessful()) {
      throw new ChroniclerException("Failed to find existing time entry item (if any) for entry, "
          + entry + ": " + Arrays.toString(response.getErrors()));
    }

    if (response.getTotalResultCount() == 0) {
      return Optional.empty();
    }

    if (response.getTotalResultCount() > 1) {
      throw new ChroniclerException("Found multiple time entry items... somethings not right.\n"
          + "Query was: " + forTimeEntryItem.getQueryFilter() + "\n"
          + "Results were: " + response.getResults());
    }

    return Optional.of(response.getResults()
        .get(0)
        .getAsJsonObject()
        .get(OBJECT_ID)
        .getAsString());
  }

  private String taskIdByNameAndWorkProduct(String task, String workProductId, String workspaceRef)
      throws IOException {
    QueryRequest forTask = new QueryRequest("task");
    QueryFilter byName = new QueryFilter("Name", "=", task);
    QueryFilter byWorkProduct = new QueryFilter("WorkProduct.ObjectID", "=", workProductId);

    forTask.setQueryFilter(byName.and(byWorkProduct));
    forTask.setWorkspace(workspaceRef);
    forTask.setFetch(new Fetch(OBJECT_ID));

    QueryResponse result = rally.query(forTask);

    if (result.getTotalResultCount() == 0) {
      throw new ChroniclerException("No tasks found for task name, " + task + ", and work product "
          + "id, " + workProductId);
    }

    return result.getResults()
        .get(0)
        .getAsJsonObject()
        .get(OBJECT_ID)
        .getAsString();
  }

  private String workProductIdByNameAndProject(String workProduct, String projectId,
      String workspaceRef) throws IOException {
    QueryRequest forWorkProduct = new QueryRequest("artifact");
    QueryFilter byName = new QueryFilter("Name", "=", workProduct);
    QueryFilter byProject = new QueryFilter("Project.ObjectID", "=", projectId);

    forWorkProduct.setQueryFilter(byName.and(byProject));
    forWorkProduct.setWorkspace(workspaceRef);
    forWorkProduct.setFetch(new Fetch(OBJECT_ID));

    QueryResponse result = rally.query(forWorkProduct);

    if (result.getTotalResultCount() == 0) {
      throw new ChroniclerException("No work products found for name, " + workProduct);
    }

    if (result.getTotalResultCount() > 1) {
      throw new ChroniclerException("Multiple work products found for name, " + workProduct
          + ". I'm afraid I may record something in the wrong place.");
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

    if (result.getTotalResultCount() > 1) {
      throw new ChroniclerException("Multiple projects found for name, " + project + ". I'm "
          + "afraid I may record something in the wrong place.");
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

  private ZonedDateTime weekStartDateUtc(ZonedDateTime date) {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        .truncatedTo(ChronoUnit.DAYS)
        .withZoneSameLocal(ISO_8601_UTC.getZone());
  }

  /**
   * If multiple entries refer to the same item, accumulate their hours by day, by item. This
   * prevents trying to create multiple time entry value's in rally for the same item for the same
   * day, which would cause a unique constraint violation. Instead we just sum any aligning entries
   * together.
   */
  private List<TimeEntry> consolidateEntries(List<TimeEntry> entries) {
    Map<CooordinatesByDay, Float> consolidatedEntries = new HashMap<>();

    // Sum each day and coordinate combination
    for (TimeEntry entry : entries) {
      TimeEntryCoordinates coordinates = entry.getCoordinates();
      CooordinatesByDay coordsByDay = new CooordinatesByDay(coordinates, entry.getDay());
      consolidatedEntries.merge(coordsByDay, entry.getHours(), Float::sum);
    }

    // Turn back into a list of entries, this time with only one entry per day / coord combo
    return consolidatedEntries.entrySet()
        .stream()
        .map(e -> new TimeEntry(e.getKey().coordinates, e.getKey().day, e.getValue()))
        .collect(Collectors.toList());
  }

  private static final class CooordinatesByDay {
    final TimeEntryCoordinates coordinates;
    final ZonedDateTime day;

    CooordinatesByDay(TimeEntryCoordinates coordinates, ZonedDateTime day) {
      this.coordinates = coordinates;
      this.day = day.truncatedTo(ChronoUnit.DAYS);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      CooordinatesByDay that = (CooordinatesByDay) o;

      return coordinates.equals(that.coordinates) && day.equals(that.day);
    }

    @Override
    public int hashCode() {
      int result = coordinates.hashCode();
      result = 31 * result + day.hashCode();
      return result;
    }
  }
}
