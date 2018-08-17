package ru.holyway.pingservice.bot.handlers.task;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import ru.holyway.pingservice.bot.message.LocalizedMessage;
import ru.holyway.pingservice.bot.message.MessageProvider;
import ru.holyway.pingservice.monitoring.TaskStatus;

public class TaskStatusResult {

  private TaskStatus taskStatus;
  private MessageProvider messageProvider;
  private String status;
  private String time;

  public TaskStatusResult(TaskStatus taskStatus, MessageProvider messageProvider) {
    this.taskStatus = taskStatus;
    this.messageProvider = messageProvider;
  }

  public String getStatus() {
    return status;
  }

  public String getTime() {
    return time;
  }

  public TaskStatusResult invoke() {
    if (taskStatus != null) {
      if (taskStatus.getSuccess()) {
        if (taskStatus.getStatus() == 200) {
          status = messageProvider.getMessage(LocalizedMessage.SUCCESS_TASK);
        } else {
          status = MessageFormat
              .format(messageProvider.getMessage(LocalizedMessage.UN_SUCCESS_TASK),
                  taskStatus.getStatus());
        }
      } else {
        status = messageProvider.getMessage(LocalizedMessage.FAILED_TASK);
      }
      final Date date = new Date(taskStatus.getTimeStamp());
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
      simpleDateFormat.applyPattern("HH:mm:ss");
      time = simpleDateFormat.format(date);
    } else {
      status = messageProvider.getMessage(LocalizedMessage.NOT_DATA);
      time = messageProvider.getMessage(LocalizedMessage.NOT_DATA);
    }
    return this;
  }
}