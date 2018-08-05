package ru.holyway.pingservice.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.holyway.pingservice.data.Task;

@Component
public class TaskSchedulerService {

  private final TaskScheduler taskScheduler;

  private final Map<String, ScheduledFuture> tasks = new ConcurrentHashMap<>();


  public TaskSchedulerService(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  public Map<String, ScheduledFuture> getTasks() {
    return tasks;
  }

  public void addTask(final Task task) {
    final ScheduledFuture scheduledFuture = taskScheduler
        .schedule(task, new CronTrigger(task.getCron()));
    tasks.put(task.getName(), scheduledFuture);
  }

  public void removeTask(final String name) {
    final ScheduledFuture scheduledFuture = tasks.get(name);
    if (scheduledFuture != null) {
      scheduledFuture.cancel(false);
    }
  }
}
