package ru.holyway.pingservice.bot.message;

public enum LocalizedMessage {

  ASK_NAME("Пожалуйста, введите название задачи", "Please enter task name"),

  ASK_URL("Пожалуйста, введите адрес сервера, который необходимо оправивать",
      "Please enter the server url which need to be pinged"),

  ASK_CRON("Пожалуйста, задайте периодичность опросов в формате cron '* * * * *'.\n "
      + "Подробнее про формат: /cron\n"
      + "Готовые пресеты:\n"
      + "/heroku - каждые 25 минут (*/25 * * * *)\n"
      + "/openshift - каждые 10 минут с 7 утра до 24 ночи (*/20 7-24 * * *)",
      "Please enter duration of pings in the cron format '* * * * *'.\n "
          + "More detailed about the format: /cron\n"
          + "Ready presets:\n"
          + "/heroku - every 25 minutes (*/25 * * * *)\n"
          + "/openshift - every 10 minutes from 7am till midnight (*/20 7-24 * * *)"),

  CONFIRMATION("Создать задачу со следующими параметрами?\n"
      + "Название: {0}\n"
      + "Адрес: {1}\n"
      + "Таймер (CRON): {2}",
      "Create task with following parameters?\n"
          + "Name: {0}\n"
          + "URL: {1}\n"
          + "Timer (CRON): {2}"),

  INCORRECT_INPUT("Введены некорректные данные! Повторите снова.",
      "Input data is incorrect! Please try again."),

  CREATED("Задача была создана успешно", "Task has been created successfully"),

  HI("Привет! Я помогу твоему приложению не умирать.", "Hi! I can help your application not die"),

  HELP("Я могу следующее:", "I can the following"),

  LANG("Я пока поддерживаю следующие языки:\n"
      + "/rus - русский\n"
      + "/eng - английский",
      "I support the following languages:\n"
          + "/rus - russian\n"
          + "/eng - english"),

  CREATE("✔ Да, создать", "✔ Yes, create"),

  CANCEL("Отменить", "Cancel"),

  CANCELED("Отменено", "Canceled"),

  DONE("Готово", "Done"),

  CRON_HELP("CRON это", "CRON it is");

  private final String rusMes;
  private final String engMes;

  LocalizedMessage(String rusMes, String engMes) {
    this.rusMes = rusMes;
    this.engMes = engMes;
  }

  public String getRusMes() {
    return rusMes;
  }

  public String getEngMes() {
    return engMes;
  }
}
