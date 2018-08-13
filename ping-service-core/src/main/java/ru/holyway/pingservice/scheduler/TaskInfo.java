package ru.holyway.pingservice.scheduler;


import java.util.concurrent.ScheduledFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class TaskInfo {

  @Nonnull
  private final Task task;

  @Nullable
  private final ScheduledFuture scheduledFuture;
}
