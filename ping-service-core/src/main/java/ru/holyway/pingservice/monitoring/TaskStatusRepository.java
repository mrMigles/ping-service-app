package ru.holyway.pingservice.monitoring;

import org.springframework.data.repository.CrudRepository;
import ru.holyway.pingservice.scheduler.Task;

public interface TaskStatusRepository extends CrudRepository<TaskStatus, Task> {

  TaskStatus findByTask(Task task);

  void deleteByTask(Task task);
}
