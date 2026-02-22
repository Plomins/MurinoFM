package com.example.murinofm;

import com.example.murinofm.entity.Track;
import com.example.murinofm.repository.TrackRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MurinoFmApplication {

    static void main(String[] args) {
        SpringApplication.run(MurinoFmApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(TrackRepository repository) {
        return _ -> {

            repository.save(new Track(null, "Я курю это кальян", "Урал Гайсин", 108));

            repository.save(new Track(null, "Numb", "Linkin Park", 185));

            System.out.println(">>> База Murino FM успешно наполнена!");
        };
    }
}