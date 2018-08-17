package ru.holyway.pingservice.bot.handlers.task;

import lombok.Data;

@Data
public class TaskMessage {

  private final Integer messageId;
  private final Long taskId;

  @Override
  public String toString() {
    return messageId + ":" + taskId;
  }
}
