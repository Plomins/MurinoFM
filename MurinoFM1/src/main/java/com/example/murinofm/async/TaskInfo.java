package com.example.murinofm.async;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Schema(description = "Информация об асинхронной задаче")
public class TaskInfo {

  @Schema(description = "Уникальный идентификатор задачи")
  private final String taskId;

  @Schema(description = "Текущий статус", example = "RUNNING")
  private TaskStatus status;

  @Schema(description = "Сообщение с результатом или ошибкой")
  private String message;

  @Schema(description = "Время запуска")
  private LocalDateTime startTime;

  @Schema(description = "Время завершения (заполняется по окончании)")
  private LocalDateTime endTime;

  public TaskInfo(String taskId) {
    this.taskId = taskId;
    this.status = TaskStatus.RUNNING;
    this.startTime = LocalDateTime.now();
  }

  public void complete(String message) {
    this.status = TaskStatus.COMPLETED;
    this.message = message;
    this.endTime = LocalDateTime.now();
  }

  public void fail(String message) {
    this.status = TaskStatus.FAILED;
    this.message = message;
    this.endTime = LocalDateTime.now();
  }
}