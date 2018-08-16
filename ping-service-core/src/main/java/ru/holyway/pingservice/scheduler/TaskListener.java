package ru.holyway.pingservice.scheduler;

import javax.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.pingservice.monitoring.TaskMonitoringService;

@Component
public class TaskListener {

  private static TaskMonitoringService taskMonitoringService;

  @Autowired
  public void setTaskStatusRepository(TaskMonitoringService taskMonitoringService) {
    TaskListener.taskMonitoringService = taskMonitoringService;
  }

  @PreRemove
  void preRemove(Task task) {
    taskMonitoringService.removeStatusForTask(task);
  }

}
