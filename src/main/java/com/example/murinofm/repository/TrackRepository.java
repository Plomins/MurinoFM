package com.example.murinofm.repository;

import com.example.murinofm.entity.Track;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями Track.
 */
@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
  /**
   * Поиск треков по исполнителю без учета регистра.
   */
  List<Track> findByArtistContainingIgnoreCase(String artist);
}