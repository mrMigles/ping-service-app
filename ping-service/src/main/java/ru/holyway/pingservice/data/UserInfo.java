package ru.holyway.pingservice.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class UserInfo {

  @Id
  @JsonProperty(required = true)
  private String name;

  @JsonProperty(required = true)
  private String password;

  @JsonIgnore
  private String role;

}
