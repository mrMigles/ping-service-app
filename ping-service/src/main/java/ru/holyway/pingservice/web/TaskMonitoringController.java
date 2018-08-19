package ru.holyway.pingservice.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.BasicAuthDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.holyway.pingservice.monitoring.TaskMonitoringService;
import ru.holyway.pingservice.monitoring.TaskStatus;
import ru.holyway.pingservice.monitoring.TaskStatusExtended;
import ru.holyway.pingservice.scheduler.Task;
import ru.holyway.pingservice.scheduler.TaskSchedulerService;

@RestController
@Api(description = "Provides API to get statuses of scheduled tasks", consumes = "application/json", authorizations = {
    @Authorization(value = "basic")
})
@BasicAuthDefinition(key = "basic")
@RequestMapping("status")
public class TaskMonitoringController {

  private final TaskSchedulerService taskSchedulerService;
  private final TaskMonitoringService taskMonitoringService;

  public TaskMonitoringController(
      TaskSchedulerService taskSchedulerService,
      TaskMonitoringService taskMonitoringService) {
    this.taskSchedulerService = taskSchedulerService;
    this.taskMonitoringService = taskMonitoringService;
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
  @ApiOperation(value = "Show status of specified task")
  public ResponseEntity<TaskStatus> getStatus(@PathVariable Long taskId) {
    final Task task = taskSchedulerService.getTask(taskId);
    final TaskStatus taskStatus = taskMonitoringService.getTaskStatus(task);
    return new ResponseEntity<>(taskStatus, HttpStatus.OK);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/extended/{taskId}", method = RequestMethod.GET)
  @ApiOperation(value = "Show status of specified task for the period")
  public ResponseEntity<TaskStatusExtended> getStatusFor(@PathVariable Long taskId,
      @RequestParam("period") Long period) {
    final Task task = taskSchedulerService.getTask(taskId);
    final TaskStatusExtended statusesForPeriod = taskMonitoringService
        .getTaskStatusesForPeriod(task, period);
    return new ResponseEntity<>(statusesForPeriod, HttpStatus.OK);
  }

}
