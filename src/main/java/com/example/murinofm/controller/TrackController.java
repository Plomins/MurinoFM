package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

  private final TrackService trackService;
  @GetMapping("/{id}")
  public TrackDto getTrackById(@PathVariable Long id) {
    return trackService.getTrackById(id);
  }
  @GetMapping
  public List<TrackDto> getTracks(@RequestParam(required = false) String title) {
    if (title != null && !title.isBlank()) {
      return trackService.searchByTitle(title);
    }
    return trackService.getAllTracks();
  }
}