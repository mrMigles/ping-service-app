package ru.holyway.pingservice.bot.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import ru.holyway.pingservice.bot.PingServiceBot;
import ru.holyway.pingservice.bot.handlers.CallbackHandler;
import ru.holyway.pingservice.bot.handlers.MessageHandler;
import ru.holyway.pingservice.usermanagement.UserManagementService;

@Configuration
public class PingServiceConfiguration {

  static {
    ApiContextInitializer.init();
  }

  @Bean
  @ConditionalOnProperty(name = "bot.enabled", havingValue = "true")
  public PingServiceBot pingServiceBotProxy(
      @Value("${bot.credential.telegram.login}") final String botName,
      @Value("${bot.credential.telegram.token}") final String botToken,
      List<MessageHandler> messageHandlers,
      List<CallbackHandler> callbackHandlers,
      UserManagementService userRepository,
      DefaultBotOptions botOptions) {
    return new PingServiceBot(botName, botToken, messageHandlers, callbackHandlers, userRepository,
        botOptions);
  }

  @Bean
  public DefaultBotOptions botOptions(@Value("${bot.config.proxy.host}") final String proxyHost,
      @Value("${bot.config.proxy.port}") final String proxyPort,
      @Value("${bot.config.proxy.pass}") final String proxyPass,
      @Value("${bot.config.proxy.user}") final String proxyUser) {
    if (!StringUtils.isEmpty(proxyUser) && !StringUtils.isEmpty(proxyPass)) {
      Authenticator.setDefault(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
        }
      });
    }

    DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

    if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {
      botOptions.setProxyHost(proxyHost);
      botOptions.setProxyPort(Integer.valueOf(proxyPort));
      botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
    }
    return botOptions;
  }
}
