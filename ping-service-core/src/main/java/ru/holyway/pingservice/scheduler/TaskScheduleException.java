package ru.holyway.pingservice.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskScheduleException extends RuntimeException {

  public enum Status {
    CONFLICT,
    RESTRICTED,
    NOT_FOUND,
    UNKNOWN
  }

  private Status status;

}
