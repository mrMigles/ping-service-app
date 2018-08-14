package ru.holyway.pingservice.monitoring;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import ru.holyway.pingservice.scheduler.Task;

interface TaskStatusRepository extends CrudRepository<TaskStatus, Integer> {

  List<TaskStatus> findByTask(final Task task);

  TaskStatus findFirstByTaskOrderByTimeStampDesc(final Task task);
}
