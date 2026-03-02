package com.example.murinofm.dto;

import com.example.murinofm.entity.Track;

/**
 * DTO для передачи данных о музыкальном треке.
 */
public record TrackDto(
    Long id,
    String title,
    String artist,
    Integer durationSeconds
) {

  /**
   * Преобразует сущность Track в объект TrackDto.
   *
   * @param track сущность трека
   * @return объект передачи данных
   */
  public static TrackDto fromEntity(Track track) {
    return new TrackDto(
        track.getId(),
        track.getTitle(),
        track.getArtist(),
        track.getDurationSeconds()
    );
  }
}