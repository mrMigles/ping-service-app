package ru.holyway.pingservice.scheduler;

import org.springframework.data.repository.CrudRepository;
import ru.holyway.pingservice.usermanagement.UserInfo;

interface TasksRepository extends CrudRepository<Task, Long> {

  Task findByNameAndUser(final String name, final UserInfo userInfo);

}
