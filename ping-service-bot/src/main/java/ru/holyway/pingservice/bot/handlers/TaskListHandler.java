package ru.holyway.pingservice.bot.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.pingservice.bot.handlers.task.TaskMessage;
import ru.holyway.pingservice.bot.handlers.task.TaskStatusResult;
import ru.holyway.pingservice.bot.message.LocalizedMessage;
import ru.holyway.pingservice.bot.message.MessageProvider;
import ru.holyway.pingservice.config.CurrentUser;
import ru.holyway.pingservice.monitoring.TaskMonitoringService;
import ru.holyway.pingservice.monitoring.TaskStatus;
import ru.holyway.pingservice.scheduler.Task;
import ru.holyway.pingservice.scheduler.TaskSchedulerService;
import ru.holyway.pingservice.security.TokenService;

@Component
@Slf4j
public class TaskListHandler extends AbstractHandler implements CallbackHandler, MessageHandler {

  private final TaskSchedulerService taskSchedulerService;
  private final TaskMonitoringService taskMonitoringService;
  private final TokenService tokenService;
  private final String serverUrl;

  protected TaskListHandler(MessageProvider messageProvider,
      TaskSchedulerService taskSchedulerService,
      TaskMonitoringService taskMonitoringService,
      TokenService tokenService,
      @Value("${server.url}") String serverUrl) {
    super(messageProvider);
    this.taskSchedulerService = taskSchedulerService;
    this.taskMonitoringService = taskMonitoringService;
    this.tokenService = tokenService;
    this.serverUrl = serverUrl;
  }

  @Override
  public boolean execute(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {
    final String callback = callbackQuery.getData();
    if (StringUtils.startsWithIgnoreCase(callback, "task:")) {
      String[] req = callback.split(":");
      if (req[1].equalsIgnoreCase("start")) {
        Task task = taskSchedulerService.startTask(Long.valueOf(req[2]));
        updateTaskInfo(sender, callbackQuery.getMessage().getMessageId(),
            callbackQuery.getMessage().getChatId(), task);
      } else if (req[1].equalsIgnoreCase("stop")) {
        Task task = taskSchedulerService.stopTask(Long.valueOf(req[2]));
        updateTaskInfo(sender, callbackQuery.getMessage().getMessageId(),
            callbackQuery.getMessage().getChatId(), task);
      } else if (req[1].equalsIgnoreCase("remove")) {
        taskSchedulerService.removeTask(Long.valueOf(req[2]));
        sender.execute(new DeleteMessage().setChatId(callbackQuery.getMessage().getChatId())
            .setMessageId(callbackQuery.getMessage().getMessageId()));
      }
      sender.execute(new AnswerCallbackQuery().setCallbackQueryId(callbackQuery.getId())
          .setText(messageProvider.getMessage(LocalizedMessage.DONE)));
      return true;
    } else if (StringUtils.startsWithIgnoreCase(callback, "update-list")) {
      String[] req = callback.split("=");
      String messageContainer = req[1];
      if (messageContainer != null) {
        String[] messages = messageContainer.split(",");
        for (String message : messages) {
          String[] ids = message.split(":");
          final Integer messageId = Integer.valueOf(ids[0]);
          final Long taskId = Long.valueOf(ids[1]);
          final Task task = taskSchedulerService.getTask(taskId);
          try {
            updateTaskInfo(sender, messageId, callbackQuery.getMessage().getChatId(), task);
          } catch (Exception e) {
            log.info("Cannot update task {} because message {} is unavailable", taskId, messageId);
          }

        }
      }
      sender.execute(new AnswerCallbackQuery().setCallbackQueryId(callbackQuery.getId())
          .setText(messageProvider.getMessage(LocalizedMessage.DONE)));
      return true;
    }
    return false;
  }

  @Override
  public boolean execute(Message message, AbsSender sender) throws TelegramApiException {
    final String textMessage = message.getText();
    if (StringUtils.startsWithIgnoreCase(textMessage, "/list")) {
      final List<Task> tasks = taskSchedulerService.getTasks();
      if (CollectionUtils.isEmpty(tasks)) {
        sendMessage(sender, message.getChatId(), LocalizedMessage.THERE_NO_TASKS);
        return true;
      }
      List<TaskMessage> taskMessages = new ArrayList<>();
      for (Task task : tasks) {
        taskMessages.add(sendTaskInfo(sender, message.getChatId(), task));
      }
      sender.execute(message(message.getChatId(), LocalizedMessage.CLICK_TO_UPDATE)
          .setReplyMarkup(getUpdateKeyboard(taskMessages)));
      return true;
    }
    return false;
  }

  private TaskMessage sendTaskInfo(final AbsSender sender, final Long chatId, final Task task)
      throws TelegramApiException {
    TaskStatus taskStatus = taskMonitoringService.getTaskStatus(task);
    String status;
    String time;
    TaskStatusResult taskStatusResult = new TaskStatusResult(taskStatus, messageProvider).invoke();
    status = taskStatusResult.getStatus();
    time = taskStatusResult.getTime();

    return new TaskMessage(sender.execute(
        message(chatId, LocalizedMessage.TASK,
            task.getName(),
            task.getUrl(),
            task.getCron(),
            task.getIsActive() ? messageProvider.getMessage(LocalizedMessage.STARTED)
                : messageProvider.getMessage(LocalizedMessage.STOPPED),
            status,
            time,
            serverUrl + "/scheduler/task/" + task.getName()
                + "?token=" + tokenService.getToken(CurrentUser.getCurrentUser().getName())
        ).setReplyMarkup(getTaskKeyboard(task))).getMessageId(), task.getId());
  }

  private void updateTaskInfo(final AbsSender sender, final Integer messageId, final Long chatId,
      final Task task)
      throws TelegramApiException {
    TaskStatus taskStatus = taskMonitoringService.getTaskStatus(task);
    String status;
    String time;
    TaskStatusResult taskStatusResult = new TaskStatusResult(taskStatus, messageProvider).invoke();
    status = taskStatusResult.getStatus();
    time = taskStatusResult.getTime();
    final String text = MessageFormat
        .format(messageProvider.getMessage(LocalizedMessage.TASK),
            task.getName(),
            task.getUrl(),
            task.getCron(),
            task.getIsActive() ? messageProvider.getMessage(LocalizedMessage.STARTED)
                : messageProvider.getMessage(LocalizedMessage.STOPPED),
            status,
            time,
            serverUrl + "/scheduler/task/" + task.getName()
                + "?token=" + tokenService.getToken(CurrentUser.getCurrentUser().getName()));
    sender.execute(new EditMessageText().setChatId(chatId).setMessageId(messageId).setText(text)
        .enableHtml(true));
    sender.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageId)
        .setReplyMarkup(getTaskKeyboard(task)));
  }

  private InlineKeyboardMarkup getTaskKeyboard(final Task task) {
    List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
    List<InlineKeyboardButton> buttons = new ArrayList<>();
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    InlineKeyboardButton submitButton = new InlineKeyboardButton("ChangeState");
    if (task.getIsActive()) {
      submitButton.setText(messageProvider.getMessage(LocalizedMessage.STOP));
      submitButton.setCallbackData("task:stop:" + task.getId());
    } else {
      submitButton.setText(messageProvider.getMessage(LocalizedMessage.START));
      submitButton.setCallbackData("task:start:" + task.getId());
    }
    buttons.add(submitButton);
    InlineKeyboardButton cancelButton = new InlineKeyboardButton("Remove");
    cancelButton.setCallbackData("task:remove:" + task.getId());
    cancelButton.setText(messageProvider.getMessage(LocalizedMessage.REMOVE));
    buttons.add(cancelButton);
    buttonList.add(buttons);
    inlineKeyboardMarkup.setKeyboard(buttonList);
    return inlineKeyboardMarkup;
  }

  private InlineKeyboardMarkup getUpdateKeyboard(final List<TaskMessage> taskMessages) {
    List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
    List<InlineKeyboardButton> buttons = new ArrayList<>();
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    InlineKeyboardButton cancelButton = new InlineKeyboardButton("Update");
    final StringJoiner joiner = new StringJoiner(",");
    taskMessages.forEach(taskMessage -> joiner.add(taskMessage.toString()));
    cancelButton.setCallbackData("update-list=" + joiner.toString());
    cancelButton.setText(messageProvider.getMessage(LocalizedMessage.UPDATE));
    buttons.add(cancelButton);
    buttonList.add(buttons);
    inlineKeyboardMarkup.setKeyboard(buttonList);
    return inlineKeyboardMarkup;
  }

}
