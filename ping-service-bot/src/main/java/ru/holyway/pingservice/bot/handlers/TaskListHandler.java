package ru.holyway.pingservice.bot.handlers;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
import ru.holyway.pingservice.bot.message.LocalizedMessage;
import ru.holyway.pingservice.bot.message.MessageProvider;
import ru.holyway.pingservice.monitoring.TaskMonitoringService;
import ru.holyway.pingservice.monitoring.TaskStatus;
import ru.holyway.pingservice.scheduler.Task;
import ru.holyway.pingservice.scheduler.TaskSchedulerService;

@Component
public class TaskListHandler extends AbstractHandler implements CallbackHandler, MessageHandler {

  private final TaskSchedulerService taskSchedulerService;
  private final TaskMonitoringService taskMonitoringService;

  protected TaskListHandler(MessageProvider messageProvider,
      TaskSchedulerService taskSchedulerService,
      TaskMonitoringService taskMonitoringService) {
    super(messageProvider);
    this.taskSchedulerService = taskSchedulerService;
    this.taskMonitoringService = taskMonitoringService;
  }

  @Override
  public boolean execute(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {
    final String callback = callbackQuery.getData();
    if (StringUtils.startsWithIgnoreCase(callback, "task:")) {
      String[] req = callback.split(":");
      if (req[1].equalsIgnoreCase("start")) {
        Task task = taskSchedulerService.startTask(req[2]);
        updateTaskInfo(sender, callbackQuery.getMessage().getMessageId(),
            callbackQuery.getMessage().getChatId(), task);
        sender.execute(new AnswerCallbackQuery().setCallbackQueryId(callbackQuery.getId())
            .setText(messageProvider.getMessage(LocalizedMessage.DONE)));
      } else if (req[1].equalsIgnoreCase("stop")) {
        Task task = taskSchedulerService.stopTask(req[2]);
        sender.execute(new AnswerCallbackQuery().setCallbackQueryId(callbackQuery.getId())
            .setText(messageProvider.getMessage(LocalizedMessage.DONE)));
        updateTaskInfo(sender, callbackQuery.getMessage().getMessageId(),
            callbackQuery.getMessage().getChatId(), task);
      } else if (req[1].equalsIgnoreCase("remove")) {
        taskSchedulerService.removeTask(req[2]);
        sender.execute(new DeleteMessage().setChatId(callbackQuery.getMessage().getChatId())
            .setMessageId(callbackQuery.getMessage().getMessageId()));
        sender.execute(new AnswerCallbackQuery().setCallbackQueryId(callbackQuery.getId())
            .setText(messageProvider.getMessage(LocalizedMessage.DONE)));
      }
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
      for (Task task : tasks) {
        sendTaskInfo(sender, message.getChatId(), task);
      }
      return true;
    }
    return false;
  }

  private void sendTaskInfo(final AbsSender sender, final Long chatId, final Task task)
      throws TelegramApiException {
    TaskStatus taskStatus = taskMonitoringService.getLastTaskStatus(task);
    String status;
    String time;
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

    sender.execute(
        message(chatId, LocalizedMessage.TASK,
            task.getName(),
            task.getUrl(),
            task.getCron(),
            task.getIsActive() ? messageProvider.getMessage(LocalizedMessage.STARTED)
                : messageProvider.getMessage(LocalizedMessage.STOPPED),
            status,
            time,
            "https://ping-services.herokuapp.com/scheduler/task/" + task.getName()
                + "?token=asdsadsadasdasdasd"
        ).setReplyMarkup(getKeyboard(task)));
  }

  private void updateTaskInfo(final AbsSender sender, final Integer messageId, final Long chatId,
      final Task task)
      throws TelegramApiException {
    TaskStatus taskStatus = taskMonitoringService.getLastTaskStatus(task);
    String status;
    String time;
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
    final String text = MessageFormat
        .format(messageProvider.getMessage(LocalizedMessage.TASK),
            task.getName(),
            task.getUrl(),
            task.getCron(),
            task.getIsActive() ? messageProvider.getMessage(LocalizedMessage.STARTED)
                : messageProvider.getMessage(LocalizedMessage.STOPPED),
            status,
            time,
            "https://ping-services.herokuapp.com/scheduler/task/" + task.getName()
                + "?token=asdsadsadasdasdasd");
    sender.execute(new EditMessageText().setChatId(chatId).setMessageId(messageId).setText(text)
        .enableHtml(true));
    sender.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageId)
        .setReplyMarkup(getKeyboard(task)));
  }

  private InlineKeyboardMarkup getKeyboard(final Task task) {
    List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
    List<InlineKeyboardButton> buttons = new ArrayList<>();
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    InlineKeyboardButton submitButton = new InlineKeyboardButton("ChangeState");
    if (task.getIsActive()) {
      submitButton.setText(messageProvider.getMessage(LocalizedMessage.STOP));
      submitButton.setCallbackData("task:stop:" + task.getName());
    } else {
      submitButton.setText(messageProvider.getMessage(LocalizedMessage.START));
      submitButton.setCallbackData("task:start:" + task.getName());
    }
    buttons.add(submitButton);
    InlineKeyboardButton cancelButton = new InlineKeyboardButton("Remove");
    cancelButton.setCallbackData("task:remove:" + task.getName());
    cancelButton.setText(messageProvider.getMessage(LocalizedMessage.REMOVE));
    buttons.add(cancelButton);
    buttonList.add(buttons);
    inlineKeyboardMarkup.setKeyboard(buttonList);
    return inlineKeyboardMarkup;
  }

}
