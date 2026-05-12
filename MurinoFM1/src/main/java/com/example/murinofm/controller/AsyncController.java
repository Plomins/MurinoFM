package com.example.murinofm.controller;

import com.example.murinofm.async.AsyncTaskService;
import com.example.murinofm.async.TaskCounter;
import com.example.murinofm.async.TaskInfo;
import com.example.murinofm.dto.TrackDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/async")
@RequiredArgsConstructor
@Tag(name = "Асинхронные задачи", description = "Запуск и мониторинг асинхронных операций (например, массовое создание треков)")
public class AsyncController {

  private final AsyncTaskService asyncTaskService;
  private final TaskCounter taskCounter;

  @Operation(summary = "Запустить асинхронное массовое создание треков",
      description = "Регистрирует задачу и сразу возвращает её идентификатор. Само создание выполняется в фоне.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Задача успешно зарегистрирована",
          content = @Content(mediaType = "text/plain", schema = @Schema(example = "Task registered: 550e8400-e29b-41d4-a716-446655440000"))),
      @ApiResponse(responseCode = "400", description = "Некорректные данные треков")
  })
  @PostMapping("/tracks/bulk")
  public String startBulkCreation(@RequestBody List<TrackDto> tracks) {
    String taskId = asyncTaskService.registerTask(tracks);
    return "Task registered: " + taskId;
  }

  @Operation(summary = "Получить статус асинхронной задачи", description = "Возвращает текущий статус задачи по её ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Статус задачи"),
      @ApiResponse(responseCode = "404", description = "Задача не найдена")
  })
  @GetMapping("/task/{taskId}")
  public ResponseEntity<TaskInfo> getTaskStatus(@PathVariable String taskId) {
    TaskInfo info = asyncTaskService.getTaskStatus(taskId);
    if (info == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found: " + taskId);
    }
    return ResponseEntity.ok(info);
  }

  @Operation(summary = "Получить статистику асинхронных задач", description = "Количество активных, завершённых и упавших задач")
  @GetMapping("/stats")
  public Map<String, Integer> getStats() {
    return taskCounter.getStats();
  }
}