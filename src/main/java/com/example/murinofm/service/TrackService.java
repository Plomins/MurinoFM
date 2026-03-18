package com.example.murinofm.service;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        .orElseThrow(() -> new RuntimeException("Трек не найден"));
  }
  public List<TrackDto> searchByTitle(String title) {
    return trackRepository.findByTitleContainingIgnoreCase(title).stream()
        .map(TrackDto::fromEntity)
        .toList();
  }
}