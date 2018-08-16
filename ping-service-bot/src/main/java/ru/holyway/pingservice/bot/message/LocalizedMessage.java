package ru.holyway.pingservice.bot.message;

public enum LocalizedMessage {

  ASK_NAME("Пожалуйста, введите <b>название задачи</b>", "Please enter task <b>name</b>"),

  ASK_URL("На какой <b>URL</b> необходимо отправлять запрос?",
      "Please enter the server <b>URL</b> to request sending"),

  ASK_CRON("Укажите <b>как часто</b> необходимо посылать запрос?"
      + "\nВ формате cron '* * * * *'. Подробнее про формат: /cron\n\n"
      + "Или используйте готовые пресеты:\n"
      + "/heroku - каждые 25 минут (*/25 * * * *)\n"
      + "/openshift - каждые 10 минут с 7 утра до 24 ночи (*/20 7-24 * * *)",
      "Please specify <b>how often</b> need to send request?\n"
          + "In the cron format '* * * * *'. More detailed about the format: /cron\n\n"
          + "Or use ready presets:\n"
          + "/heroku - every 25 minutes (*/25 * * * *)\n"
          + "/openshift - every 10 minutes from 7am till midnight (*/20 7-24 * * *)"),

  CONFIRMATION("Создать задачу со следующими параметрами?\n"
      + "<b>✏️ Название:</b> {0}\n"
      + "<b>\uD83C\uDF10 URL:</b> {1}\n"
      + "<b>\uD83D\uDD50 Таймер (CRON):</b> {2}",
      "Create task with following parameters?\n"
          + "<b>✏️ Name:</b> {0}\n"
          + "<b>\uD83C\uDF10 URL:</b> {1}\n"
          + "<b>\uD83D\uDD50 Timer (CRON):</b> {2}"),

  INCORRECT_INPUT("Введены некорректные данные! Повторите снова.",
      "Input data is incorrect! Please try again."),

  CREATED("Задача была создана успешно", "Task has been created successfully"),

  NAME_EXIST("Задача с таким имененм уже сущесвует", "Task with this name already exist"),

  HI("Привет!\nЯ предоставляю упрощенный фуникцонал сервиса <a href=\"https://ping-services.herokuapp.com\">Ping Service</a>\n"
      + "Я помогу твоему приложению быть всегда доступным и на дам усыпить его. \n"
      + "Я делаю это путем отправки периодических ping запросов на указанный Вами URL и указанной периодичностью.\n"
      + "Создай своё первое задание для меня  /new\n"
      + "Посмотреть список задач  /list\n"
      + "Помощь по командам  /help\n\n"
      + "Сменнить язык  /lang",
      "Hi!\nI provide light functional of service <a href=\"https://ping-services.herokuapp.com\">Ping Service</a>\n"
          + "I can help your app be available always and I won't let its sleep.\n"
          + "I do it via sending periodical ping requests on specified URL with specified frequency.\n"
          + "Create your first task for me  /new\n"
          + "Show list of tasks  /list\n"
          + "Help  /help\n\n"
          + "Change the language  /lang"),

  HELP("Я могу следующее:\n"
      + "/new - создать новое задание\n"
      + "/list - посмотреть список заданий\n"
      + "/lang - сменить язык\n"
      + "/settings - настройки\n",

      "I can the following:\n"
          + "/new - create new task\n"
          + "/list - show list of the tasks\n"
          + "/lang - change the language\n"
          + "/settings - settings\n"),

  CRON("* * * * *\n"
      + "Минуты Часы Дни_месяца Месяцы Дни_недели Годы\n"
      + "*/N - каждые N минут/часов/...\n"
      + "N-N - между N и N\n"
      + "N - в N минут/часов/...\n"
      + "Пример: */10 6-23 * * * - значит каждые 10 минут, с 6 утра до 23 часов, каждый день",
      "Minutes Hours Day Of Months Months Day of Week Years"),

  LANG("Выберите один из следующий доступных языков:\n"
      + "/rus - \uD83C\uDDF7\uD83C\uDDFA русский \n"
      + "/eng - \uD83C\uDDEC\uD83C\uDDE7 английский",
      "Select one of the following available languages:\n"
          + "/rus - russian\n"
          + "/eng - english"),

  CREATE("✔ Да, создать", "✔ Yes, create"),

  CANCEL("❌ Отменить", "❌ Cancel"),

  REMOVE("\uD83D\uDEAE Удалить", "\uD83D\uDEAE Remove"),

  STOP("\u23F9 Остановить", "\u23F9 Stop"),

  START("▶️ Запустить", "▶️ Start"),

  CANCELED("Отменено", "Canceled"),

  STARTED("Активно", "In Progress"),

  STOPPED("Остановлено", "Stopped"),

  THERE_NO_TASKS("Нет задач", "There no tasks"),

  DONE("\uD83D\uDC4D Готово", "\uD83D\uDC4D Done"),

  NOT_DONE("\uD83D\uDC4E Ошибка", "\uD83D\uDC4E Error"),

  UN_SUCCESS_TASK("⚠️ ({0})", "⚠️ ({0})"),

  SUCCESS_TASK("✅ (200)", "✅ (200)"),

  NOT_DATA("Н/д", "N/A"),

  FAILED_TASK("\uD83D\uDED1", "\uD83D\uDED1"),


  TASK("<b>Название:</b> {0}\n"
      + "<b>URL:</b> {1}\n"
      + "<b>CRON:</b> {2}\n"
      + "<b>Состояние:</b> {3}\n"
      + "<b>Последний статус:</b> {4} на {5}\n\n"
      + "<a href=\"{6}\">Посмотреть статистику \uD83D\uDCC8</a>",
      "<b>Name: {0}</b>\n"
          + "<b>URL:</b> {1}\n"
          + "<b>CRON:</b> {2}\n"
          + "<b>Status:</b> {3}\n"
          + "<b>Last result:</b> {4} on {5}\n\n"
          + "<a href=\"{6}\">Show statistics \uD83D\uDCC8</a>"),

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
