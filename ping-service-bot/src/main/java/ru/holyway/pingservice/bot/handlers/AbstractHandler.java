package ru.holyway.pingservice.bot.handlers;

import java.text.MessageFormat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.pingservice.bot.message.LocalizedMessage;
import ru.holyway.pingservice.bot.message.MessageProvider;

public abstract class AbstractHandler {

  protected final MessageProvider messageProvider;

  protected AbstractHandler(MessageProvider messageProvider) {
    this.messageProvider = messageProvider;
  }

  protected void sendMessage(final AbsSender sender, final Long chatId,
      final LocalizedMessage message) throws TelegramApiException {
    sender.execute(message(chatId, message).enableHtml(true));
  }

  protected void sendMessage(final AbsSender sender, final Long chatId,
      final LocalizedMessage message, final Object... args) throws TelegramApiException {
    sender.execute(message(chatId, message, args).enableHtml(true));
  }

  protected SendMessage message(final Long chatId, final LocalizedMessage message) {
    final String localizedMessage = messageProvider.getMessage(message);
    return new SendMessage().setChatId(chatId).setText(localizedMessage).enableHtml(true);
  }

  protected SendMessage message(final Long chatId, final LocalizedMessage message,
      final Object... args) {
    final String formattedMessage = MessageFormat.format(messageProvider.getMessage(message), args);
    return new SendMessage().setChatId(chatId).setText(formattedMessage).enableHtml(true);
  }


}
