package ru.holyway.pingservice.bot.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.pingservice.bot.message.LocalizedMessage;
import ru.holyway.pingservice.bot.message.MessageProvider;
import ru.holyway.pingservice.data.Task;
import ru.holyway.pingservice.scheduler.TaskSchedulerService;

@Component
public class NewTaskHandler implements MessageHandler, CallbackHandler {

  public NewTaskHandler(MessageProvider messageProvider,
      TaskSchedulerService schedulerClient) {
    this.messageProvider = messageProvider;
    this.schedulerClient = schedulerClient;
  }

  private enum State {
    NOT_STATE,
    ASK_NAME,
    ASK_URL,
    ASK_DURATION,
    CONFIRMATION
  }

  private final Map<Integer, State> stateOfUSers = new ConcurrentHashMap<>();
  private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();

  private final MessageProvider messageProvider;
  private final TaskSchedulerService schedulerClient;

  @Override
  public boolean execute(Message message, AbsSender sender)
      throws TelegramApiException {

    final String textMessage = message.getText();
    if (StringUtils.isNotEmpty(textMessage)) {
      final Integer userId = message.getFrom().getId();
      final State state = getState(userId);
      if (state != State.NOT_STATE) {
        if (StringUtils.containsIgnoreCase("/cancel", textMessage)) {
          sender.execute(
              new SendMessage().setChatId(message.getChatId()).setText(messageProvider.getMessage(
                  LocalizedMessage.CANCELED)));
          stateOfUSers.put(userId, State.NOT_STATE);
          return true;
        }
      }
      if (state == State.NOT_STATE) {
        if (StringUtils.containsIgnoreCase("/new", textMessage)) {
          sender.execute(
              new SendMessage().setChatId(message.getChatId()).setText(messageProvider.getMessage(
                  LocalizedMessage.ASK_NAME)));
          stateOfUSers.put(userId, State.ASK_NAME);
        }
      } else if (state == State.ASK_NAME) {
        Task task = new Task();
        task.setName(textMessage);
        tasks.put(userId, task);

        sender.execute(
            new SendMessage().setChatId(message.getChatId()).setText(messageProvider.getMessage(
                LocalizedMessage.ASK_URL)));
        stateOfUSers.put(userId, State.ASK_URL);

      } else if (state == State.ASK_URL) {
        Task task = tasks.get(userId);
        if (task != null) {
          task.setUrl(textMessage);

          sender.execute(
              new SendMessage().setChatId(message.getChatId()).setText(messageProvider.getMessage(
                  LocalizedMessage.ASK_CRON)));
          stateOfUSers.put(userId, State.ASK_DURATION);

        } else {
          stateOfUSers.put(userId, State.NOT_STATE);
        }
      } else if (state == State.ASK_DURATION) {
        Task task = tasks.get(userId);
        if (task != null) {
          if (textMessage.matches(".{1,5} .{1,5} .{1,5} .{1,5} .{1,5}")) {
            task.setCron("0 " + textMessage.trim());
            sender.execute(
                new SendMessage().setChatId(message.getChatId()).setText(
                    MessageFormat.format(messageProvider.getMessage(LocalizedMessage.CONFIRMATION),
                        task.getName(), task.getUrl(), task.getCron()))
                    .setReplyMarkup(getKeyboard()));
            stateOfUSers.put(userId, State.CONFIRMATION);
          } else if (textMessage.startsWith("/")) {
            if (StringUtils.startsWithIgnoreCase(textMessage, "/heroku")) {
              task.setCron("0 */25 * * * *");
            } else if (StringUtils.startsWithIgnoreCase(textMessage, "/openshift")) {
              task.setCron("0 */20 7-24 * * *");
            }
            sender.execute(
                new SendMessage().setChatId(message.getChatId()).setText(
                    MessageFormat.format(messageProvider.getMessage(LocalizedMessage.CONFIRMATION),
                        task.getName(), task.getUrl(), task.getCron()))
                    .setReplyMarkup(getKeyboard()));
            stateOfUSers.put(userId, State.CONFIRMATION);
          } else {
            sender.execute(
                new SendMessage().setChatId(message.getChatId()).setText(messageProvider.getMessage(
                    LocalizedMessage.INCORRECT_INPUT)));
          }

        } else {
          stateOfUSers.put(userId, State.NOT_STATE);
        }
      }
      return true;
    }

    return false;
  }

  @Override
  public boolean execute(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {
    final String callback = callbackQuery.getData();
    final Integer userId = callbackQuery.getFrom().getId();
    final State state = getState(userId);
    if (state == State.CONFIRMATION) {
      if (StringUtils.isNotEmpty(callback)) {
        if (callback.equalsIgnoreCase("new:submit")) {
          final Task task = tasks.get(userId);
          task.setIsActive(true);
          schedulerClient.addTask(task);
          sender.execute(
              new SendMessage().setChatId(callbackQuery.getMessage().getChatId())
                  .setText(messageProvider.getMessage(
                      LocalizedMessage.CREATED)));
        } else {
          sender.execute(
              new SendMessage().setChatId(callbackQuery.getMessage().getChatId())
                  .setText(messageProvider.getMessage(
                      LocalizedMessage.CANCELED)));
        }
        stateOfUSers.put(userId, State.NOT_STATE);
      }
    }
    return false;
  }

  private State getState(final Integer userId) {
    return stateOfUSers.getOrDefault(userId, State.NOT_STATE);
  }

  private InlineKeyboardMarkup getKeyboard() {
    List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
    List<InlineKeyboardButton> buttons = new ArrayList<>();
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    InlineKeyboardButton submitButton = new InlineKeyboardButton("Submit");
    submitButton.setText("✔ Да, создать");
    submitButton.setCallbackData("new:submit");
    buttons.add(submitButton);
    InlineKeyboardButton cancelButton = new InlineKeyboardButton("Cancel");
    cancelButton.setCallbackData("new:cancel");
    cancelButton.setText("Отменить");
    buttons.add(cancelButton);
    buttonList.add(buttons);
    inlineKeyboardMarkup.setKeyboard(buttonList);
    return inlineKeyboardMarkup;
  }
}
