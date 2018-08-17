package ru.holyway.pingservice.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.holyway.pingservice.config.CurrentUser;
import ru.holyway.pingservice.scheduler.TaskScheduleException.Status;
import ru.holyway.pingservice.usermanagement.UserInfo;

@Component
public class TaskSchedulerService {

  private final TaskScheduler taskScheduler;

  private final TasksRepository tasksRepository;

  private final TaskRunService taskRunService;

  private final HashMap<Long, TaskInfo> scheduledTasks = new HashMap<>();

  public TaskSchedulerService(TaskScheduler taskScheduler,
      TasksRepository tasksRepository,
      TaskRunService taskRunService) {
    this.taskScheduler = taskScheduler;
    this.tasksRepository = tasksRepository;
    this.taskRunService = taskRunService;
  }

  @PostConstruct
  private void initTasks() {
    tasksRepository.findAll().forEach(this::scheduleTask);
  }

  public List<Task> getTasks() {
    List<Task> tasks = scheduledTasks.values().stream().map(TaskInfo::getTask)
        .collect(Collectors.toList());
    return tasks.stream().filter(this::checkAccess).collect(Collectors.toList());
  }

  @Transactional
  public Task addTask(final Task task) {
    return addTask(task, true);
  }

  private Task addTask(final Task task, final boolean isNew) {
    final UserInfo userInfo = CurrentUser.getCurrentUser();
    if (userInfo != null && !isAdmin(userInfo)) {
      task.setUser(userInfo);
    }
    if (isNew && getTasks().contains(task)) {
      throw new TaskScheduleException(Status.CONFLICT);
    }
    Task newTask = tasksRepository.save(task);
    scheduleTask(newTask);
    return newTask;
  }

  private void scheduleTask(final Task task) {
    ScheduledFuture scheduledFuture = null;

    if (task.getIsActive() == null || task.getIsActive()) {
      scheduledFuture = taskScheduler
          .schedule(new TaskRunContainer(task, taskRunService), new CronTrigger(task.getCron()));
    }
    scheduledTasks.put(task.getId(), new TaskInfo(task, scheduledFuture));
  }

  public Task updateTask(final Task task) {
    final TaskInfo taskInfo = scheduledTasks.get(task.getId());
    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final ScheduledFuture scheduledFuture = taskInfo.getScheduledFuture();
      if (scheduledFuture != null) {
        scheduledFuture.cancel(false);
      }
      return addTask(task, false);
    } else {
      throw new TaskScheduleException(Status.NOT_FOUND);
    }
  }

  public Task startTask(final Long id) {
    final TaskInfo taskInfo = scheduledTasks.get(id);

    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final Task task = taskInfo.getTask();
      task.setIsActive(true);
      return updateTask(task);
    } else {
      throw new TaskScheduleException(Status.NOT_FOUND);
    }
  }

  public Task stopTask(final Long id) {
    final TaskInfo taskInfo = scheduledTasks.get(id);

    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final Task task = taskInfo.getTask();
      task.setIsActive(false);
      return updateTask(task);
    } else {
      throw new TaskScheduleException(Status.NOT_FOUND);
    }
  }

  public void removeTask(final Long id) {
    final TaskInfo taskInfo = scheduledTasks.get(id);

    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final ScheduledFuture scheduledFuture = taskInfo.getScheduledFuture();
      if (scheduledFuture != null) {
        scheduledFuture.cancel(false);
      }
      tasksRepository.delete(taskInfo.getTask());
      scheduledTasks.remove(id);
    } else {
      throw new TaskScheduleException(Status.NOT_FOUND);
    }
  }

  /**
   * LOLOLOLO
   */
  private boolean checkAccess(final Task task) {
    final UserInfo userInfo = CurrentUser.getCurrentUser();
    if (isAdmin(userInfo)) {
      return true;
    }
    return task.getUser() != null && task.getUser().getName().equals(userInfo.getName());
  }

  /**
   * LOLOLOOLOLOLOLOLLOLOLOLOLOLOLOLOL
   */
  private boolean isAdmin(UserInfo user) {
    return user.getRole().equalsIgnoreCase("ROLE_ADMIN");
  }

}
