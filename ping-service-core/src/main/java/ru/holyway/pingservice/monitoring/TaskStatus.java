package ru.holyway.pingservice.monitoring;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Data;
import ru.holyway.pingservice.scheduler.Task;

@Entity
@Data
public class TaskStatus {

  @Id
  @GeneratedValue
  private Integer id;

  private Long timeStamp;

  private Long duration;

  private Integer status;

  private Boolean success;

  public TaskStatus() {
  }

  public TaskStatus(Long timeStamp, Long duration, Integer status, Boolean success,
      Task task) {
    this.timeStamp = timeStamp;
    this.duration = duration;
    this.status = status;
    this.success = success;
    this.task = task;
  }

  @OneToOne
  @JoinColumn(name = "task_id")
  private Task task;
}
