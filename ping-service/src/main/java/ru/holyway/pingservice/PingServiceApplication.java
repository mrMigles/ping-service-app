package ru.holyway.pingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;


@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
@SpringBootApplication
@EnableScheduling
public class PingServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PingServiceApplication.class, args);
  }
}
