package ru.holyway.pingservice.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.net.URI;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor
@Data
@Slf4j
@Entity
@ApiModel(value = "Scheduled task")
public class Task implements Serializable {

  @JsonProperty(required = true)
  @ApiModelProperty(value = "Scheduled interval fot the task in CRON format",
      example = "0 */10 * * * *",
      notes =
          "CRON format for the task when: *(sec) *(min) *(hours) *(day) *(month) *(day of week)."
              + " E.g. value \"0 */10 * * * *\' means every 10 minutes")
  private String cron;

  @JsonProperty(required = true)
  @Id
  @ApiModelProperty(value = "Name of task - should be unique")
  private String name;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserInfo user;

  @JsonProperty(required = true)
  @ApiModelProperty(value = "URL to ping",
      example = "http://myservice/health")
  private String url;

  @JsonProperty(defaultValue = "GET")
  @ApiModelProperty(value = "Method of request",
      example = "GET", allowableValues = "GET,POST,PUT,DELETE")
  private String method;

  @JsonProperty(defaultValue = "true")
  @ApiModelProperty(value = "State of task",
      allowableValues = "true,false")
  private Boolean isActive;
}
