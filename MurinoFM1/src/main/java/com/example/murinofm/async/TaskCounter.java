package com.example.murinofm.async;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

@Component
public class TaskCounter {

  private final AtomicInteger activeTasks = new AtomicInteger(0);
  private final AtomicInteger totalCompleted = new AtomicInteger(0);
  private final AtomicInteger totalFailed = new AtomicInteger(0);

  public void incrementActive() {
    activeTasks.incrementAndGet();
  }

  public void decrementActive() {
    activeTasks.decrementAndGet();
  }

  public void addCompleted() {
    totalCompleted.incrementAndGet();
    decrementActive();
  }

  public void addFailed() {
    totalFailed.incrementAndGet();
    decrementActive();
  }

  public int getActive() {
    return activeTasks.get();
  }

  public int getTotalCompleted() {
    return totalCompleted.get();
  }

  public int getTotalFailed() {
    return totalFailed.get();
  }

  public Map<String, Integer> getStats() {
    return Map.of(
        "active", getActive(),
        "completed", getTotalCompleted(),
        "failed", getTotalFailed()
    );
  }
}