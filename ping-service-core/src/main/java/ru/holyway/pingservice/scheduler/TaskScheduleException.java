package ru.holyway.pingservice.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@AllArgsConstructor

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TaskScheduleException extends RuntimeException {

  public enum Status {
    CONFLICT,
    RESTRICTED,
    NOT_FOUND,
    UNKNOWN
  }

  private Status status;

}
