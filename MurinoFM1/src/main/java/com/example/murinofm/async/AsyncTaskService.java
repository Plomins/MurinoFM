package com.example.murinofm.async;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.TrackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncTaskService {

  private final ConcurrentHashMap<String, TaskInfo> tasks = new ConcurrentHashMap<>();
  private final TrackService trackService;
  private final TaskCounter taskCounter;

  public TaskInfo getTaskStatus(String taskId) {
    return tasks.get(taskId);
  }

  @Async("taskExecutor")
  public void runBulkTrackCreation(List<TrackDto> tracks, String taskId) {
    TaskInfo info = tasks.get(taskId);
    taskCounter.incrementActive();
    try {
      log.info("Async task {} started", taskId);
      trackService.bulkCreate(tracks);
      info.complete("Successfully created " + tracks.size() + " tracks");
      taskCounter.addCompleted();
      log.info("Async task {} completed", taskId);
    } catch (Exception e) {
      info.fail("Error: " + e.getMessage());
      taskCounter.addFailed();
      log.error("Async task {} failed", taskId, e);
    }
  }

  public String registerTask(List<TrackDto> tracks) {
    String taskId = UUID.randomUUID().toString();
    TaskInfo info = new TaskInfo(taskId);
    tasks.put(taskId, info);
    runBulkTrackCreation(tracks, taskId);
    return taskId;
  }
}