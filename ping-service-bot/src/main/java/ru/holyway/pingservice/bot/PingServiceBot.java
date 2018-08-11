package ru.holyway.pingservice.bot;

import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import ru.holyway.pingservice.bot.handlers.CallbackHandler;
import ru.holyway.pingservice.bot.handlers.MessageHandler;
import ru.holyway.pingservice.config.CurrentUser;
import ru.holyway.pingservice.data.UserInfo;
import ru.holyway.pingservice.data.UserRepository;

@Slf4j
public class PingServiceBot extends TelegramLongPollingBot {

  private final String botName;
  private final String botToken;
  private final List<MessageHandler> messageHandlers;
  private final List<CallbackHandler> callbackHandlers;
  private final UserRepository userRepository;

  private BotSession session;

  public PingServiceBot(String botName, String botToken,
      List<MessageHandler> messageHandlers,
      List<CallbackHandler> callbackHandlers,
      UserRepository userRepository) {
    this.botName = botName;
    this.botToken = botToken;
    this.messageHandlers = messageHandlers;
    this.callbackHandlers = callbackHandlers;
    this.userRepository = userRepository;
  }

  public PingServiceBot(String botName, String botToken,
      List<MessageHandler> messageHandlers,
      List<CallbackHandler> callbackHandlers,
      UserRepository userRepository,
      DefaultBotOptions defaultBotOptions) {
    super(defaultBotOptions);
    this.botName = botName;
    this.botToken = botToken;
    this.messageHandlers = messageHandlers;
    this.callbackHandlers = callbackHandlers;
    this.userRepository = userRepository;
  }

  @PostConstruct
  private void init() {
    TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
    try {
      session = telegramBotsApi.registerBot(this);
    } catch (Exception e) {
      log.error("Error: {}", e.getMessage(), e);
    }
  }

  @Override
  public void onUpdateReceived(Update update) {
    try {
      if (update.hasCallbackQuery()) {
        initCurrentUser(update.getCallbackQuery().getFrom());
        for (CallbackHandler callbackHandler : callbackHandlers) {
          final boolean ans = callbackHandler.execute(update.getCallbackQuery(), this);
          if (ans) {
            return;
          }
        }
      }
      if (update.hasMessage()) {
        initCurrentUser(update.getMessage().getFrom());
        for (MessageHandler messageHandler : messageHandlers) {
          final boolean ans = messageHandler.execute(update.getMessage(), this);
          if (ans) {
            return;
          }
        }
      }

    } catch (TelegramApiException e) {
      log.error("Error: {}", e);
    } finally {
      CurrentUser.clear();
    }
  }

  @Override
  public String getBotUsername() {
    return botName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  private void initCurrentUser(final User user) {
    UserInfo currentUserInfo = userRepository.findOne(String.valueOf(user.getId()));
    if (currentUserInfo == null) {
      currentUserInfo = new UserInfo(String.valueOf(user.getId()),
          UUID.randomUUID().toString().replaceAll("-", ""),
          "ROLE_USER",
          user.getLanguageCode());
      userRepository.save(currentUserInfo);
    }
    CurrentUser.setCurrentUser(currentUserInfo);
  }

  @PreDestroy
  public void destroy() {
    if (session != null) {
      session.stop();
    }
  }
}
