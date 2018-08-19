package ru.holyway.pingservice.web;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.holyway.pingservice.usermanagement.UserInfo;
import ru.holyway.pingservice.usermanagement.UserManagementService;

@RestController
@RequestMapping("users")
public class UserController {

  private final UserManagementService userRepository;

  public UserController(UserManagementService userRepository) {
    this.userRepository = userRepository;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/", method = RequestMethod.PUT)
  public ResponseEntity<String> createUser(@RequestBody UserInfo user) {
    user.setRole("ROLE_USER");
    userRepository.addUser(user);
    return new ResponseEntity<>("Created user with name " + user.getName(), HttpStatus.CREATED);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ResponseEntity<List<UserInfo>> getUsers() {
    return new ResponseEntity<>(
        userRepository.getUsers(),
        HttpStatus.OK);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/{userName}", method = RequestMethod.DELETE)
  public ResponseEntity<String> removeUser(@PathVariable final String userName) {
    userRepository.removeUser(userName);
    return new ResponseEntity<>("User has been removed", HttpStatus.NO_CONTENT);
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(value = "/{userName}/changePassword", method = RequestMethod.DELETE)
  public ResponseEntity<String> changePassword(@PathVariable final String userName,
      @RequestParam(name = "password") final String password) {
    userRepository.changePassword(userName, password);
    return new ResponseEntity<>("Password has been changed", HttpStatus.OK);
  }
}
