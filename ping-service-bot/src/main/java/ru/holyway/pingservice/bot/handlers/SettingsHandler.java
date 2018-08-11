package ru.holyway.pingservice.bot.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SettingsHandler implements MessageHandler {

  @Override
  public boolean execute(Message message, AbsSender sender)
      throws TelegramApiException {
    return false;
  }
}
