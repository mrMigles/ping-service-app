package ru.holyway.pingservice.data;

import java.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TaskInfo {

  private final Task task;

  private final ScheduledFuture scheduledFuture;

}
