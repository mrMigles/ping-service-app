package ru.holyway.pingservice.bot.handlers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.pingservice.bot.message.LocalizedMessage;
import ru.holyway.pingservice.bot.message.MessageProvider;

@Component
public class HelpHandler extends AbstractHandler implements MessageHandler {

  protected HelpHandler(MessageProvider messageProvider) {
    super(messageProvider);
  }

  @Override
  public boolean execute(Message message, AbsSender sender)
      throws TelegramApiException {
    final String textMessage = message.getText();
    if (StringUtils.isNotEmpty(textMessage)) {
      if (StringUtils.containsIgnoreCase(textMessage, "/help")) {
        sendMessage(sender, message.getChatId(), LocalizedMessage.HELP);
        return true;
      }
      if (StringUtils.containsIgnoreCase(textMessage, "/cron")) {
        sendMessage(sender, message.getChatId(), LocalizedMessage.CRON);
        return true;
      }
    }
    return false;
  }
}
