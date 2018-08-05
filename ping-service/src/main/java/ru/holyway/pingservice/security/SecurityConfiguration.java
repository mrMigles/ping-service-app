package ru.holyway.pingservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends
    WebSecurityConfigurerAdapter {

  @Value("${auth.internal.user}")
  private String userName;

  @Value("${auth.internal.pass}")
  private String userPass;

  @Autowired
  private DefaultUserDetailService userDetailsService;

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser(userName).password(userPass)
        .authorities("ROLE_USER", "ROLE_ADMIN");
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
        .ignoring()
        .antMatchers("/resources/**", "/health"); // #3
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .httpBasic();
    http.authorizeRequests()
        .antMatchers("/*", "/index.html").hasRole("USER")
        .and()
        .logout()
        .permitAll();
    http.userDetailsService(userDetailsService);
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
