package ru.holyway.pingservice.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CallbackHandler {

  /**
   * @param callbackQuery
   */
  boolean execute(final CallbackQuery callbackQuery, final AbsSender sender)
      throws TelegramApiException;

}
