package ru.holyway.pingservice.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.holyway.pingservice.data.UserInfo;
import ru.holyway.pingservice.data.UserRepository;

@RestController
@RequestMapping("users")
public class UserController {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  public UserController(UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/", method = RequestMethod.PUT)
  public ResponseEntity<String> createUser(@RequestBody UserInfo user) {
    user.setRole("ROLE_USER");
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    return new ResponseEntity<>("Created user with name " + user.getName(), HttpStatus.CREATED);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ResponseEntity<List<UserInfo>> getUsers() {
    return new ResponseEntity<>(
        new ArrayList<>((Collection<? extends UserInfo>) userRepository.findAll()),
        HttpStatus.OK);
  }
}
