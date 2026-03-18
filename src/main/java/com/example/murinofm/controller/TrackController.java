package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

  private final TrackService trackService;

  // READ: Получение всех ИЛИ поиск по названию (GET /api/tracks?title=...)
  @GetMapping
  public List<TrackDto> getTracks(@RequestParam(required = false) String title) {
    if (title != null && !title.isBlank()) {
      return trackService.searchByTitle(title);
    }
    return trackService.getAllTracks();
  }

  // READ: Получение одного по ID (GET /api/tracks/1)
  @GetMapping("/{id}")
  public TrackDto getOne(@PathVariable Long id) {
    return trackService.getTrackById(id);
  }


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TrackDto create(@RequestBody TrackDto dto) {
    return trackService.save(dto);
  }

  @PutMapping("/{id}")
  public TrackDto update(@PathVariable Long id, @RequestBody TrackDto dto) {
    return trackService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    trackService.delete(id);
  }
}