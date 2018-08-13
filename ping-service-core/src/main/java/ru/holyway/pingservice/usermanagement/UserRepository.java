package ru.holyway.pingservice.usermanagement;

import org.springframework.data.repository.CrudRepository;

interface UserRepository extends CrudRepository<UserInfo, String> {

}
