package com.example.murinofm.service;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.repository.TrackRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервисный слой для управления музыкальными треками.
 */
@Service
@RequiredArgsConstructor
public class TrackService {

  private final TrackRepository trackRepository;

  /**
   * Возвращает список всех доступных треков.
   *
   * @return список TrackDto
   */
  public List<TrackDto> getAllTracks() {
    return trackRepository.findAll().stream()
        .map(TrackDto::fromEntity)
        .collect(Collectors.toList());
  }

  /**
   * Выполняет поиск треков по имени исполнителя.
   *
   * @param artist имя или часть имени исполнителя
   * @return список найденных TrackDto
   */
  public List<TrackDto> searchByArtist(String artist) {
    return trackRepository.findByArtistContainingIgnoreCase(artist).stream()
        .map(TrackDto::fromEntity)
        .collect(Collectors.toList());
  }

  /**
   * Находит конкретный трек по его идентификатору.
   *
   * @param id идентификатор трека
   * @return найденный TrackDto
   * @throws RuntimeException если трек с указанным ID не найден
   */
  public TrackDto getTrackById(Long id) {
    return trackRepository.findById(id)
        .map(TrackDto::fromEntity)
        .orElseThrow(() -> new RuntimeException("Трек с ID " + id + " не найден"));
  }
}