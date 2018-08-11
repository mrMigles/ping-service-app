package ru.holyway.pingservice.bot.handlers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelpHandler implements MessageHandler {

  @Override
  public boolean execute(Message message, AbsSender sender)
      throws TelegramApiException {
    final String textMessage = message.getText();
    if (StringUtils.isNotEmpty(textMessage)) {
      if (StringUtils.containsIgnoreCase(textMessage, "/help")) {
        sender.execute(new SendMessage().setChatId(message.getChatId()).setText("Help"));
        return true;
      }
      if (StringUtils.containsIgnoreCase(textMessage, "/curlHelp")) {
        sender.execute(new SendMessage().setChatId(message.getChatId()).setText("Help"));
        return true;
      }
    }
    return false;
  }
}
