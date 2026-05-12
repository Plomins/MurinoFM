package com.example.murinofm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
@Tag(name = "Демонстрация race condition", description = "Показывает проблему гонки данных и её решение через AtomicInteger")
public class RaceConditionController {

  @GetMapping("/race-condition")
  @Operation(summary = "Запустить тест race condition",
      description = "Запускает 50 потоков, каждый увеличивает счётчик 1000 раз. "
          + "Сравнивается обычный int и AtomicInteger. Возвращает ожидаемые и фактические значения.")
  public Map<String, Object> demonstrateRaceCondition() throws InterruptedException {
    final int threads = 50;
    final int incrementsPerThread = 1000;
    final int expectedTotal = threads * incrementsPerThread;

    final int[] unsafeCounter = {0};
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    for (int i = 0; i < threads; i++) {
      executor.submit(() -> {
        for (int j = 0; j < incrementsPerThread; j++) {
          unsafeCounter[0]++;
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
    int unsafeResult = unsafeCounter[0];
    AtomicInteger atomicCounter = new AtomicInteger(0);
    executor = Executors.newFixedThreadPool(threads);
    for (int i = 0; i < threads; i++) {
      executor.submit(() -> {
        for (int j = 0; j < incrementsPerThread; j++) {
          atomicCounter.incrementAndGet(); // атомарно
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
    int atomicResult = atomicCounter.get();
    Map<String, Object> result = new HashMap<>();
    result.put("expected", expectedTotal);
    result.put("unsafeCounterResult", unsafeResult);
    result.put("unsafeCounterLoss", expectedTotal - unsafeResult);
    result.put("atomicCounterResult", atomicResult);
    result.put("atomicCounterLoss", expectedTotal - atomicResult);
    result.put("conclusion", atomicResult == expectedTotal
        ? "AtomicInteger работает корректно, race condition отсутствует"
        : "Ошибка: проверьте код");

    return result;
  }
}