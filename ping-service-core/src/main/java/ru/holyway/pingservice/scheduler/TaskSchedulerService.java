package ru.holyway.pingservice.scheduler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
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

  private final Set<TaskInfo> scheduledTasks = new HashSet<>();

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
    List<Task> tasks = scheduledTasks.stream().map(TaskInfo::getTask).collect(Collectors.toList());
    return tasks.stream().filter(this::checkAccess).collect(Collectors.toList());
  }

  @Transactional
  public Task addTask(final Task task) {
    return addTask(task, true);
  }

  private Task addTask(final Task task, final boolean isNew) {
    final UserInfo userInfo = CurrentUser.getCurrentUser();
    if (userInfo != null) {
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
    scheduledTasks.add(new TaskInfo(task, scheduledFuture));
  }

  public Task updateTask(final Task task) {
    final UserInfo userInfo = CurrentUser.getCurrentUser();
    if (userInfo != null) {
      task.setUser(userInfo);
    }
    final TaskInfo taskInfo = scheduledTasks.stream()
        .filter(taskInfo1 -> taskInfo1.getTask().equals(task)).findFirst().orElse(null);
    if (taskInfo != null) {
      task.setId(taskInfo.getTask().getId());
      final ScheduledFuture scheduledFuture = taskInfo.getScheduledFuture();
      if (scheduledFuture != null) {
        scheduledFuture.cancel(false);
      }
      return addTask(task, false);
    } else {
      throw new TaskScheduleException(Status.NOT_FOUND);
    }
  }

  public Task startTask(final String name) {
    final TaskInfo taskInfo = getTaskInfo(name);

    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final Task task = taskInfo.getTask();
      task.setIsActive(true);
      return updateTask(task);
    } else {
      throw new TaskScheduleException(Status.NOT_FOUND);
    }
  }

  public Task stopTask(final String name) {
    final TaskInfo taskInfo = getTaskInfo(name);

    if (taskInfo != null) {
      final Task task = taskInfo.getTask();
      task.setIsActive(false);
      return updateTask(task);
    } else {
      throw new TaskScheduleException(Status.NOT_FOUND);
    }
  }

  public void removeTask(final String name) {
    final TaskInfo taskInfo = getTaskInfo(name);

    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final ScheduledFuture scheduledFuture = taskInfo.getScheduledFuture();
      if (scheduledFuture != null) {
        scheduledFuture.cancel(false);
      }
      tasksRepository.delete(taskInfo.getTask());
      scheduledTasks.remove(taskInfo);
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
    if (user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
      return true;
    }
    return false;
  }

  @Nullable
  private TaskInfo getTaskInfo(String name) {
    final UserInfo userInfo = CurrentUser.getCurrentUser();
    return scheduledTasks.stream().filter(
        taskInfo1 -> taskInfo1.getTask().getName().equals(name) && taskInfo1.getTask().getUser()
            .equals(userInfo)).findFirst().orElse(null);
  }

}
