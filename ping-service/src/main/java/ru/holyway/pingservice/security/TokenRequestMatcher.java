package ru.holyway.pingservice.security;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class TokenRequestMatcher implements RequestMatcher {

  @Override
  public boolean matches(HttpServletRequest request) {
    return request.getParameter("token") != null;
  }
}
