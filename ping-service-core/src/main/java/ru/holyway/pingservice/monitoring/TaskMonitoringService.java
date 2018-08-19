package ru.holyway.pingservice.monitoring;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult.Result;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Component;
import ru.holyway.pingservice.scheduler.Task;

@Component
@Slf4j
public class TaskMonitoringService {

  private final TaskStatusRepository taskStatusRepository;
  private final InfluxDBTemplate<Point> influxDBTemplate;

  public TaskMonitoringService(
      TaskStatusRepository taskStatusRepository,
      InfluxDBTemplate<Point> influxDBTemplate) {
    this.taskStatusRepository = taskStatusRepository;
    this.influxDBTemplate = influxDBTemplate;
  }

  public TaskStatus getTaskStatus(final Task task) {
    if (task != null) {
      return taskStatusRepository.findByTask(task);
    }
    return null;
  }

  public TaskStatusExtended getTaskStatusesForPeriod(final Task task, final Long period) {
    List<Result> results = influxDBTemplate
        .query(new Query("select * from task_" + task.getId(), "data")).getResults();
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
