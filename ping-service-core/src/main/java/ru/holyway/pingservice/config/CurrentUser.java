package ru.holyway.pingservice.config;


import ru.holyway.pingservice.usermanagement.UserInfo;

public class CurrentUser {

  private static ThreadLocal<UserInfo> currentUser = new ThreadLocal<>();

  public static void setCurrentUser(final UserInfo userId) {
    currentUser.set(userId);
  }

  public static UserInfo getCurrentUser() {
    return currentUser.get();
  }

  public static void clear() {
    currentUser.remove();
  }

}
