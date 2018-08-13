package ru.holyway.pingservice.usermanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class UserInfo {

  public UserInfo(String name, String role) {
    this.name = name;
    this.role = role;
  }

  public UserInfo(String name, String password, String role, String lang) {
    this.name = name;
    this.password = password;
    this.role = role;
    this.lang = lang;
  }

  @Id
  @JsonProperty(required = true)
  private String name;

  @JsonProperty(required = true)
  private String password;

  private String role;

  private String lang;
}
