package com.example.murinofm;

import com.example.murinofm.entity.Track;
import com.example.murinofm.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Главный класс приложения MurinoFm.
 * Запускает Spring Boot приложение и инициализирует базу данных тестовыми треками.
 */
@Slf4j
@SpringBootApplication
public class MurinoFmApplication {
  /**
   * Точка входа в приложение.
   *
   * @param args аргументы командной строки
   */
  public static void main(String[] args) {
    SpringApplication.run(MurinoFmApplication.class, args);
  }

  @Bean
  CommandLineRunner initDatabase(TrackRepository repository) {
    return args -> {
      repository.save(new Track(null, "Numb", "Linkin Park", 185));
      log.info(">>> База Murino FM успешно наполнена!");
    };
  }
}