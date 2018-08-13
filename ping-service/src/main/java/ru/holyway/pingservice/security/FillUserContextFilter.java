package ru.holyway.pingservice.security;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import ru.holyway.pingservice.config.CurrentUser;
import ru.holyway.pingservice.usermanagement.UserInfo;
import ru.holyway.pingservice.usermanagement.UserManagementService;

@Component
public class FillUserContextFilter implements Filter {

  final UserManagementService userRepository;

  public FillUserContextFilter(UserManagementService userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null) {
        Object principal = authentication.getPrincipal();
        if (principal != null && principal instanceof User) {
          final User user = (User) principal;
          final String role = user.getAuthorities().stream().findFirst().get().getAuthority();
          final UserInfo currentUserInfo = Optional
              .ofNullable(userRepository.getUser(user.getUsername()))
              .orElse(new UserInfo(user.getUsername(), role));
          CurrentUser.setCurrentUser(currentUserInfo);
        }
      }
    } finally {
      filterChain.doFilter(servletRequest, servletResponse);
    }

  }

  @Override
  public void destroy() {

  }
}
