package ru.holyway.pingservice.bot.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.holyway.pingservice.config.CurrentUser;

@Component
public class MessageProvider {

  public String getMessage(LocalizedMessage message) {
    final String langCode = CurrentUser.getCurrentUser().getLang();
    if (langCode != null && StringUtils.containsIgnoreCase(langCode, "ru")) {
      return message.getRusMes();
    }
    return message.getEngMes();
  }

}
