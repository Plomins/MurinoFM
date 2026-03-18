package com.example.murinofm.service;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.entity.Track;
import com.example.murinofm.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

  private final TrackRepository trackRepository;

  public List<TrackDto> getAllTracks() {
    return trackRepository.findAll().stream()
        .map(TrackDto::fromEntity)
        .toList();
  }

  public TrackDto getTrackById(Long id) {
    return trackRepository.findById(id)
        .map(TrackDto::fromEntity)
        // Sonar Fix: используем специфичное исключение
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found"));
  }

  public List<TrackDto> searchByTitle(String title) {
    return trackRepository.findByTitleContainingIgnoreCase(title).stream()
        .map(TrackDto::fromEntity)
        .toList();
  }

  @Transactional
  public TrackDto save(TrackDto dto) {
    Track track = new Track();
    track.setTitle(dto.getTitle());
    track.setDurationSeconds(dto.getDurationSeconds());
    return TrackDto.fromEntity(trackRepository.save(track));
  }

  @Transactional
  public TrackDto update(Long id, TrackDto dto) {
    Track track = trackRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found"));
    track.setTitle(dto.getTitle());
    track.setDurationSeconds(dto.getDurationSeconds());
    return TrackDto.fromEntity(trackRepository.save(track));
  }

  @Transactional
  public void delete(Long id) {
    if (!trackRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found");
    }
    trackRepository.deleteById(id);
  }
}