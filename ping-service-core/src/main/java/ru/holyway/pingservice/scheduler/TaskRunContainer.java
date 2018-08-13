package ru.holyway.pingservice.scheduler;

import lombok.Data;

@Data
public class TaskRunContainer implements Runnable {

  private final Task task;

  private final TaskRunService taskRunService;

  @Override
  public void run() {
    taskRunService.run(task);
  }
}
