package ru.holyway.pingservice.scheduler;

import org.springframework.data.repository.CrudRepository;

interface TasksRepository extends CrudRepository<Task, String> {

}
