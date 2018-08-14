package ru.holyway.pingservice.security;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private final TokenService tokenService;

  protected TokenAuthenticationFilter(
      RequestMatcher requiresAuthenticationRequestMatcher,
      TokenService tokenService) {
    super(requiresAuthenticationRequestMatcher);
    this.tokenService = tokenService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    final String token = request.getParameter("token");
    final String userName = tokenService.getUserByToken(token);
    if (StringUtils.isNotEmpty(userName)) {
      tokenService.invalidateToken(userName);
      return new UsernamePasswordAuthenticationToken(userName, token,
          Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }
    throw new AccountExpiredException("Token is invalid or expired");
  }

  @Override
  protected boolean requiresAuthentication(HttpServletRequest request,
      HttpServletResponse response) {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return super.requiresAuthentication(request, response)
        && (authentication == null || authentication instanceof AnonymousAuthenticationToken);
  }

  @Override
  @Autowired
  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    super.setAuthenticationManager(authenticationManager);
  }
}
