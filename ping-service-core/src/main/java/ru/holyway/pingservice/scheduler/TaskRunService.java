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
import ru.holyway.pingservice.data.Task;

@Component
@Slf4j
public class TaskRunService {

  private final InfluxDBTemplate<Point> influxDBTemplate;


  public TaskRunService(
      InfluxDBTemplate<Point> influxDBTemplate) {
    this.influxDBTemplate = influxDBTemplate;
  }

  public void run(final Task task) {
    final String url = task.getUrl();
    HttpStatus httpStatus;
    try {
      ResponseEntity responseEntity = new RestTemplate()
          .getForEntity(URI.create(url), String.class);
      httpStatus = responseEntity.getStatusCode();
    } catch (HttpStatusCodeException exception) {
      httpStatus = exception.getStatusCode();
    }
    if (influxDBTemplate != null) {
      final Point p = Point.measurement(task.getName())
          .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
          .tag("tenant", "default")
          .addField("status", httpStatus.value())
          .build();
      influxDBTemplate.write(p);
    }
    log.info("Request {}", url);
  }
}
