package ru.holyway.pingservice.usermanagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class UserInfo implements Serializable {

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

  @JsonIgnore
  private String password;

  private String role;

  private String lang;

  private Boolean initial;

  private String mail;
}
