package ru.holyway.pingservice.web;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.holyway.pingservice.data.Task;
import ru.holyway.pingservice.data.TaskInfo;
import ru.holyway.pingservice.scheduler.TaskSchedulerService;

@RestController
@RequestMapping("tasks")
public class TaskController {

  private final TaskSchedulerService taskSchedulerService;

  public TaskController(TaskSchedulerService taskSchedulerService) {
    this.taskSchedulerService = taskSchedulerService;
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/", method = RequestMethod.PUT)
  public ResponseEntity<String> createTask(@RequestBody Task task) {
    taskSchedulerService.addTask(task);
    return new ResponseEntity<>("Created with name " + task.getName(), HttpStatus.CREATED);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ResponseEntity<List<TaskInfo>> getTasks() {
    return new ResponseEntity<>(taskSchedulerService.getTasks(), HttpStatus.OK);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/{taskName}", method = RequestMethod.DELETE)
  public ResponseEntity<String> removeTask(@PathVariable String taskName) {
    taskSchedulerService.removeTask(taskName);
    return new ResponseEntity<>("Removed", HttpStatus.OK);
  }
}
