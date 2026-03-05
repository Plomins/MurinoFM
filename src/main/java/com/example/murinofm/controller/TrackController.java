package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.TrackService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("checkstyle:MissingJavadocMethod")

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {
  private final TrackService trackService;

  @GetMapping("/{id}")
  public TrackDto getTrack(@PathVariable Long id) {
    return trackService.getTrackById(id);
  }

  @GetMapping
  public List<TrackDto> searchTracksByArtist(@RequestParam(required = false) String artist) {
    if (artist != null) {
      return trackService.searchByArtist(artist);
    }
    return List.of();
  }
}