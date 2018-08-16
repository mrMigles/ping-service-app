package ru.holyway.pingservice.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.holyway.pingservice.scheduler.Task;

@Component
@Slf4j
public class TaskMonitoringService {

  private final TaskStatusRepository taskStatusRepository;

  public TaskMonitoringService(
      TaskStatusRepository taskStatusRepository) {
    this.taskStatusRepository = taskStatusRepository;
  }

  public TaskStatus getTaskStatus(final Task task) {
    if (task != null) {
      return taskStatusRepository.findByTask(task);
    }
    return null;
  }

  public void saveResult(final TaskStatus taskStatus) {
    final TaskStatus current = taskStatusRepository.findByTask(taskStatus.getTask());
    if (current != null) {
      taskStatus.setId(current.getId());
    }
    taskStatusRepository.save(taskStatus);
  }

  public void removeStatusForTask(final Task task) {
    taskStatusRepository.deleteByTask(task);
  }
}
