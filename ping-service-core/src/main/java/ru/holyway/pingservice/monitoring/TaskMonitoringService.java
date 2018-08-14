package ru.holyway.pingservice.monitoring;

import java.util.Collections;
import java.util.List;
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

  public List<TaskStatus> getTaskStatuses(final Task task) {
    if (task != null) {
      return taskStatusRepository.findByTask(task);
    }
    return Collections.emptyList();
  }

  public TaskStatus getLastTaskStatus(final Task task) {
    if (task != null) {
      return taskStatusRepository.findFirstByTaskOrderByTimeStampDesc(task);
    }
    return null;
  }

  public void saveResult(final TaskStatus taskStatus) {
    taskStatusRepository.save(taskStatus);
  }
}
