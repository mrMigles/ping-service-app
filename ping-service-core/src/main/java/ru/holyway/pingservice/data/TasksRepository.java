package ru.holyway.pingservice.data;

import org.springframework.data.repository.CrudRepository;

public interface TasksRepository extends CrudRepository<Task, String> {

}
