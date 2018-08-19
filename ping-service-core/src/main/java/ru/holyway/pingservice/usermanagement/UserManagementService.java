package ru.holyway.pingservice.usermanagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class UserManagementService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final Map<String, UserInfo> userInfoMap = new ConcurrentHashMap<>();

  public UserManagementService(UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostConstruct
  private void initUsers() {
    userRepository.findAll().forEach(userInfo -> userInfoMap.put(userInfo.getName(), userInfo));
  }

  public List<UserInfo> getUsers() {
    return Collections.unmodifiableList(new ArrayList<>(userInfoMap.values()));
  }

  public UserInfo addUser(final UserInfo userInfo) {
    if (userInfoMap.get(userInfo.getName()) != null) {
      throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
          "User with this name already exist");
    }
    userInfo.setPassword(null);
    userRepository.save(userInfo);
    userInfoMap.put(userInfo.getName(), userInfo);
    return userInfo;
  }

  public UserInfo getUser(final String name) {
    return userInfoMap.get(name);
  }

  public UserInfo updateUser(final UserInfo userInfo) {
    final String pass = userInfo.getPassword();
    if (StringUtils.isNotEmpty(pass)) {
      userInfo.setPassword(passwordEncoder.encode(pass));
    }
    userRepository.save(userInfo);
    userInfoMap.put(userInfo.getName(), userInfo);
    return userInfo;
  }


  public void removeUser(final String name) {
    userInfoMap.remove(name);
    userRepository.delete(name);
  }

  public void changePassword(final String userName, final String newPassword) {
    final UserInfo userInfo = userInfoMap.get(userName);
    if (userInfo != null & StringUtils.isNotEmpty(newPassword)) {
      userInfo.setPassword(passwordEncoder.encode(newPassword));
      userRepository.save(userInfo);
      userInfoMap.put(userInfo.getName(), userInfo);
    }
  }
}
