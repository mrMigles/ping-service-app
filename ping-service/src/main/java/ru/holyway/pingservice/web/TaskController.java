package ru.holyway.pingservice.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.BasicAuthDefinition;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.holyway.pingservice.scheduler.Task;
import ru.holyway.pingservice.scheduler.TaskSchedulerService;

@RestController
@Api(description = "Provides API to manage scheduled tasks", consumes = "application/json", authorizations = {
    @Authorization(value = "basic")
})
@BasicAuthDefinition(key = "basic")
@RequestMapping("tasks")
public class TaskController {

  private final TaskSchedulerService taskSchedulerService;

  public TaskController(TaskSchedulerService taskSchedulerService) {
    this.taskSchedulerService = taskSchedulerService;
  }

  @PreAuthorize("hasRole('USER')")
  @ApiOperation(value = "Create a new task")
  @RequestMapping(value = "/", method = RequestMethod.PUT)
  @BasicAuthDefinition(key = "basic")
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    return new ResponseEntity<>(taskSchedulerService.addTask(task), HttpStatus.CREATED);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/", method = RequestMethod.GET)
  @ApiOperation(value = "Return all tasks of this user")
  public ResponseEntity<List<Task>> getTasks() {
    return new ResponseEntity<>(taskSchedulerService.getTasks(), HttpStatus.OK);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
  @ApiOperation(value = "Return task by id")
  public ResponseEntity<Task> getTask(@PathVariable Long taskId) {
    return ResponseEntity.ok(taskSchedulerService.getTask(taskId));
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ApiOperation(value = "Update specified task")
  public ResponseEntity<String> updateTask(@RequestBody Task task) {
    taskSchedulerService.updateTask(task);
    return new ResponseEntity<>("Updated with name " + task.getName(), HttpStatus.CREATED);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/{taskId}", method = RequestMethod.DELETE)
  @ApiOperation(value = "Remove specified task")
  public ResponseEntity<String> removeTask(@PathVariable Long taskId) {
    taskSchedulerService.removeTask(taskId);
    return new ResponseEntity<>("Removed", HttpStatus.OK);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/{taskId}/start", method = RequestMethod.POST)
  @ApiOperation(value = "Run specified task")
  public ResponseEntity<String> startTask(@PathVariable Long taskId) {
    taskSchedulerService.startTask(taskId);
    return new ResponseEntity<>("Started", HttpStatus.OK);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/{taskId}/stop", method = RequestMethod.POST)
  @ApiOperation(value = "Stop specified task")
  public ResponseEntity<String> stopTask(@PathVariable Long taskId) {
    taskSchedulerService.stopTask(taskId);
    return new ResponseEntity<>("Stopped", HttpStatus.OK);
  }
}
