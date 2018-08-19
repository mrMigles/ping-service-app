package ru.holyway.pingservice.monitoring;

import java.util.Map;
import lombok.Data;
import ru.holyway.pingservice.scheduler.Task;

@Data
public class TaskStatusExtended extends TaskStatus {

  private Map<Long, Long> dutations;

  private Map<Long, Integer> statuses;

  public TaskStatusExtended(final TaskStatus taskStatus) {
    super(taskStatus.getTimeStamp(), taskStatus.getDuration(), taskStatus.getStatus(),
        taskStatus.getSuccess(), taskStatus.getTask());
  }

  public TaskStatusExtended(Long timeStamp, Long duration, Integer status, Boolean success,
      Task task) {
    super(timeStamp, duration, status, success, task);
  }
}
