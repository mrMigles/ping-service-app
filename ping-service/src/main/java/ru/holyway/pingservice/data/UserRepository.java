package ru.holyway.pingservice.data;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserInfo, String> {

}
