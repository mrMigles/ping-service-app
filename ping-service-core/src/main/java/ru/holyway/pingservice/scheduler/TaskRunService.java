package ru.holyway.pingservice.scheduler;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.holyway.pingservice.monitoring.TaskMonitoringService;
import ru.holyway.pingservice.monitoring.TaskStatus;

@Component
@Slf4j
public class TaskRunService {

  private final InfluxDBTemplate<Point> influxDBTemplate;
  private final TaskMonitoringService taskMonitoringService;


  public TaskRunService(
      InfluxDBTemplate<Point> influxDBTemplate,
      TaskMonitoringService taskMonitoringService) {
    this.influxDBTemplate = influxDBTemplate;
    this.taskMonitoringService = taskMonitoringService;
  }

  public void run(final Task task) {
    final String url = task.getUrl();
    HttpStatus httpStatus = null;
    final Long start = System.currentTimeMillis();
    try {
      ResponseEntity responseEntity = new RestTemplate()
          .getForEntity(URI.create(url), String.class);
      httpStatus = responseEntity.getStatusCode();
    } catch (HttpStatusCodeException exception) {
      httpStatus = exception.getStatusCode();
    } catch (Exception e) {
      log.info(e.getMessage(), e);
    }
    final Long end = System.currentTimeMillis();
    final Integer status = httpStatus != null ? httpStatus.value() : -1;
    writeToInflux(task, status, end - start);

    final TaskStatus taskStatus = new TaskStatus(end, end - start, status,
        status != -1, task);
    taskMonitoringService.saveResult(taskStatus);
    log.info("Request {}", url);
  }

  private void writeToInflux(Task task, Integer status, Long duration) {
    if (influxDBTemplate != null) {
      try {
        final Point p = Point.measurement("task_" + task.getId())
            .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .tag("tenant", "default")
            .addField("status", status)
            .addField("duration", duration)
            .build();
        influxDBTemplate.write(p);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
