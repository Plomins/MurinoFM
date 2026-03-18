package com.example.murinofm.repository;

import com.example.murinofm.entity.Track;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

  // Это метод для обычного поиска
  List<Track> findByTitleContainingIgnoreCase(String title);

  // А это решение проблемы N+1 через EntityGraph!
  // Мы говорим Spring вытянуть связанные альбомы и их артистов ОДНИМ запросом.
  @Override
  @EntityGraph(attributePaths = {"album", "album.artist"})
  List<Track> findAll();
}