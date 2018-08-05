package ru.holyway.pingservice.scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
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

  private final Map<String, TaskInfo> tasks = new ConcurrentHashMap<>();

  public TaskSchedulerService(TaskScheduler taskScheduler,
      TasksRepository tasksRepository, UserRepository userRepository) {
    this.taskScheduler = taskScheduler;
    this.tasksRepository = tasksRepository;
    this.userRepository = userRepository;
  }

  @PostConstruct
  private void initTasks() {
    tasksRepository.findAll().forEach(this::addTaskInternal);
  }

  public List<TaskInfo> getTasks() {
    return tasks.values().stream().filter(taskInfo -> {
      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
        return true;
      }
      UserInfo userInfo = taskInfo.getTask().getUser();
      if (userInfo == null) {
        return true;
      }
      return user.getUsername().equals(userInfo.getName());
    }).collect(Collectors.toList());
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
    final ScheduledFuture scheduledFuture = taskScheduler
        .schedule(task, new CronTrigger(task.getCron()));
    tasksRepository.save(task);
    tasks.put(task.getName(), new TaskInfo(task, scheduledFuture));
  }

  public void removeTask(final String name) {
    final TaskInfo taskInfo = tasks.get(name);
    if (taskInfo != null) {
      final ScheduledFuture scheduledFuture = tasks.get(name).getScheduledFuture();
      scheduledFuture.cancel(false);
      tasksRepository.delete(name);
    }
  }
}
