package ru.holyway.pingservice.bot.handlers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.pingservice.bot.message.LocalizedMessage;
import ru.holyway.pingservice.bot.message.MessageProvider;
import ru.holyway.pingservice.config.CurrentUser;
import ru.holyway.pingservice.usermanagement.UserInfo;
import ru.holyway.pingservice.usermanagement.UserManagementService;

@Component
public class SettingsHandler implements MessageHandler {

  private final MessageProvider messageProvider;
  private final UserManagementService userManagementService;

  public SettingsHandler(MessageProvider messageProvider,
      UserManagementService userManagementService) {
    this.messageProvider = messageProvider;
    this.userManagementService = userManagementService;
  }

  @Override
  public boolean execute(Message message, AbsSender sender)
      throws TelegramApiException {
    final String textMessage = message.getText();
    if (StringUtils.isNotEmpty(textMessage)) {
      final UserInfo userInfo = CurrentUser.getCurrentUser();
      if (StringUtils.startsWithIgnoreCase(textMessage, "/start")) {
        sender.execute(new SendMessage().setChatId(message.getChatId())
            .setText(messageProvider.getMessage(LocalizedMessage.HI)));
        return true;
      }
      if (StringUtils.startsWithIgnoreCase(textMessage, "/settings")) {
        return true;
      }
      if (StringUtils.startsWithIgnoreCase(textMessage, "/lang")) {
        sender.execute(new SendMessage().setChatId(message.getChatId())
            .setText(messageProvider.getMessage(LocalizedMessage.LANG)));
        return true;
      }
      if (StringUtils.startsWithIgnoreCase(textMessage, "/rus")) {
        userInfo.setLang("rus");
        userManagementService.updateUser(userInfo);
        CurrentUser.setCurrentUser(userInfo);
        sender.execute(new SendMessage().setChatId(message.getChatId())
            .setText(messageProvider.getMessage(LocalizedMessage.DONE)));
        return true;
      }
      if (StringUtils.startsWithIgnoreCase(textMessage, "/eng")) {
        userInfo.setLang("eng");
        userManagementService.updateUser(userInfo);
        CurrentUser.setCurrentUser(userInfo);
        sender.execute(new SendMessage().setChatId(message.getChatId())
            .setText(messageProvider.getMessage(LocalizedMessage.DONE)));
        return true;
      }
    }
    return false;
  }
}
