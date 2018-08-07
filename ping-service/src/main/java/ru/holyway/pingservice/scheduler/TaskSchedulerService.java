package ru.holyway.pingservice.scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import ru.holyway.pingservice.data.Task;
import ru.holyway.pingservice.data.TaskInfo;
import ru.holyway.pingservice.data.TasksRepository;
import ru.holyway.pingservice.data.UserInfo;
import ru.holyway.pingservice.data.UserRepository;

@Component
public class TaskSchedulerService {

  private final TaskScheduler taskScheduler;

  private final TasksRepository tasksRepository;

  private final UserRepository userRepository;

  private final TaskRunService taskRunService;

  private final Map<String, TaskInfo> scheduledTasks = new ConcurrentHashMap<>();

  public TaskSchedulerService(TaskScheduler taskScheduler,
      TasksRepository tasksRepository, UserRepository userRepository,
      TaskRunService taskRunService) {
    this.taskScheduler = taskScheduler;
    this.tasksRepository = tasksRepository;
    this.userRepository = userRepository;
    this.taskRunService = taskRunService;
  }

  @PostConstruct
  private void initTasks() {
    tasksRepository.findAll().forEach(this::addTaskInternal);
  }

  public List<Task> getTasks() {

    List<Task> tasks = scheduledTasks.values().stream().map(TaskInfo::getTask).collect(
        Collectors.toList());

    return tasks.stream().filter(this::checkAccess).collect(Collectors.toList());
  }

  public void addTask(final Task task) {
    final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final String userName = user.getUsername();
    final UserInfo userInfo = userRepository.findOne(userName);
    if (userInfo != null) {
      task.setUser(userInfo);
    }
    addTaskInternal(task);
  }

  private void addTaskInternal(final Task task) {
    ScheduledFuture scheduledFuture = null;

    if (task.getIsActive() == null || task.getIsActive()) {
      scheduledFuture = taskScheduler
          .schedule(new TaskRunContainer(task, taskRunService), new CronTrigger(task.getCron()));
    }
    tasksRepository.save(task);
    scheduledTasks.put(task.getName(), new TaskInfo(task, scheduledFuture));
  }

  public void updateTask(final Task task) {
    final TaskInfo taskInfo = scheduledTasks.get(task.getName());
    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final ScheduledFuture scheduledFuture = taskInfo.getScheduledFuture();
      if (scheduledFuture != null) {
        scheduledFuture.cancel(false);
      }
      addTask(task);
    } else {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
  }

  public void startTask(final String name) {
    final TaskInfo taskInfo = scheduledTasks.get(name);
    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final Task task = taskInfo.getTask();
      task.setIsActive(true);
      updateTask(task);
    } else {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
  }

  public void stopTask(final String name) {
    final TaskInfo taskInfo = scheduledTasks.get(name);
    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final Task task = taskInfo.getTask();
      task.setIsActive(false);
      updateTask(task);
    } else {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
  }

  public void removeTask(final String name) {
    final TaskInfo taskInfo = scheduledTasks.get(name);
    if (taskInfo != null && checkAccess(taskInfo.getTask())) {
      final ScheduledFuture scheduledFuture = scheduledTasks.get(name).getScheduledFuture();
      if (scheduledFuture != null) {
        scheduledFuture.cancel(false);
      }
      tasksRepository.delete(name);
      scheduledTasks.remove(name);
    } else {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * LOLOLOLO
   */
  private boolean checkAccess(final Task task) {
    final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (isAdmin(user)) {
      return true;
    }
    return task.getUser() != null && task.getUser().getName().equals(user.getUsername());
  }

  /**
   * LOLOLOOLOLOLOLOLLOLOLOLOLOLOLOLOL
   */
  private boolean isAdmin(User user) {
    if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      return true;
    }
    return false;
  }
}
