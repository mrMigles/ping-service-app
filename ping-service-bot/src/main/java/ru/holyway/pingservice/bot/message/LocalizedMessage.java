package ru.holyway.pingservice.bot.message;

public enum LocalizedMessage {

  ASK_NAME("Пожалуйста, введите <b>название задачи</b>", "Please enter task name"),

  ASK_URL("Введите адрес сервера, для отправки запроса",
      "Please enter the server url to request sending"),

  ASK_CRON("Укажите <b>как часто</b> необходимо посылать запрос. \nВ формате cron '* * * * *'. "
      + "Подробнее про формат: /cron\n\n"
      + "Готовые пресеты:\n"
      + "/heroku - каждые 25 минут (*/25 * * * *)\n"
      + "/openshift - каждые 10 минут с 7 утра до 24 ночи (*/20 7-24 * * *)",
      "Please enter duration of pings in the cron format '* * * * *'.\n"
          + "More detailed about the format: /cron\n"
          + "Ready presets:\n"
          + "/heroku - every 25 minutes (*/25 * * * *)\n"
          + "/openshift - every 10 minutes from 7am till midnight (*/20 7-24 * * *)"),

  CONFIRMATION("Создать задачу со следующими параметрами?\n"
      + "<b>Название:</b> {0}\n"
      + "<b>Адрес:</b> {1}\n"
      + "<b>Таймер (CRON):</b> {2}",
      "Create task with following parameters?\n"
          + "<b>Name:</b> {0}\n"
          + "<b>URL:</b> {1}\n"
          + "<b>Timer (CRON):</b> {2}"),

  INCORRECT_INPUT("Введены некорректные данные! Повторите снова.",
      "Input data is incorrect! Please try again."),

  CREATED("Задача была создана успешно", "Task has been created successfully"),

  HI("Привет!\nЯ предоставляю упрощенный фуникцонал сервиса <a href=\"https://ping-services.herokuapp.com\">Ping Service</a>\n"
      + "Я помогу твоему приложению быть всегда доступным, путем отправки периодических ping запросов.\n"
      + "Создай своё первое задание для меня  /new\n"
      + "Посмотреть список задач  /list\n"
      + "Помощь по командам  /help\n\n"
      + "Сменнить язык  /lang",
      "Hi!\nI provide light functional of service <a href=\"https://ping-services.herokuapp.com\">Ping Service</a>\n"
          + "I can help your app be available always, due sending periodical ping requests.\n"
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
          + "/new -create new task\n"
          + "/list - show list of the tasks\n"
          + "/lang - change the language\n"
          + "/settings - settings\n"),

  CRON("* * * * *\n"
      + "<Минуты> <Часы> <Дни_месяца> <Месяцы> <Дни_недели> <Годы>\n"
      + "*/N - каждые N минут/часов/...\n"
      + "N-N - между N и N\n"
      + "N - в N минут/часов/...\n"
      + "Пример: */10 6-23 * * * - значит каждые 10 минут, с 6 утра до 23 часов, каждый день",
      "<Minutes> <Hours> <Day Of Months> <Months> <Day of Week> <Years>"),

  LANG("Выберите один из следующий доступных языков:\n"
      + "/rus - русский\n"
      + "/eng - английский",
      "Select one of the following available languages:\n"
          + "/rus - russian\n"
          + "/eng - english"),

  CREATE("✔ Да, создать", "✔ Yes, create"),

  CANCEL("Отменить", "Cancel"),

  REMOVE("Удалить", "Remove"),

  STOP("Остановить", "Stop"),

  START("Запустить", "Start"),

  CANCELED("Отменено", "Canceled"),

  THERE_NO_TASKS("Нет задач", "There no tasks"),

  DONE("Готово", "Done"),

  TASK("<b>Название:</b> {0}\n"
      + "<b>URL:</b> {1}\n"
      + "<b>CRON:</b> {2}\n"
      + "<b>Статус:</b> {3}"
      + "",
      "<b>Name: {0}</b>\n"
          + "<b>URL:</b> {1}\n"
          + "<b>CRON:</b> {2}\n"
          + "<b>Status:</b> {3}"),

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
