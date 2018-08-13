package ru.holyway.pingservice.security;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.holyway.pingservice.usermanagement.UserInfo;
import ru.holyway.pingservice.usermanagement.UserManagementService;

@Component(value = "userDetailService")
public class DefaultUserDetailService implements UserDetailsService {

  private final UserManagementService userRepository;

  public DefaultUserDetailService(UserManagementService userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final UserInfo userInfo = userRepository.getUser(username);
    if (userInfo == null) {
      throw new UsernameNotFoundException("Cannot find the user");
    }
    return new User(userInfo.getName(), userInfo.getPassword(),
        Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole())));
  }
}
