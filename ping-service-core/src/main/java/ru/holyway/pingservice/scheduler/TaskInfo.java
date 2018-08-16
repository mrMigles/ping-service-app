package ru.holyway.pingservice.scheduler;


import java.util.concurrent.ScheduledFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TaskInfo taskInfo = (TaskInfo) o;

    return task.equals(taskInfo.task);
  }

  @Override
  public int hashCode() {
    return task.hashCode();
  }
}
