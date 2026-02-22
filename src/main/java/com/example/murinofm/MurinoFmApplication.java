package com.example.murinofm;

import com.example.murinofm.entity.Track;
import com.example.murinofm.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j; // Импорт для логгера
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j // Аннотация Lombok для создания логгера
@SpringBootApplication
public class MurinoFmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MurinoFmApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(TrackRepository repository) {
        return _ -> {
            repository.save(new Track(null, "Я курю это кальян", "Урал Гайсин", 108));
            repository.save(new Track(null, "Numb", "Linkin Park", 185));

            // Заменяем System.out на логгер, как просил SonarCloud
            log.info(">>> База Murino FM успешно наполнена!");
        };
    }
}