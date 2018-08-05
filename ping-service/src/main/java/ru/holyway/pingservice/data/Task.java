package ru.holyway.pingservice.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.net.URI;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor
@Data
@Slf4j
public class Task implements Runnable, Serializable {

  @JsonProperty(required = true)
  private String cron;

  @JsonProperty(required = true)
  private String name;

  @JsonProperty(required = true)
  private String url;

  @JsonProperty(required = false, defaultValue = "GET")
  private String method;

  @Override
  public void run() {
    new RestTemplate().getForEntity(URI.create(url), String.class);
    log.info("Request {}", url);
  }
}
