package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.TrackService;
import com.example.murinofm.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TrackController {

  private final TrackService trackService;
  private final ArtistService artistService;

  // 1. Получение всех треков (GET /api/tracks)

  @GetMapping("/tracks")
  public List<TrackDto> getTracks(@RequestParam(required = false) String title) {
    return (title != null && !title.isBlank())
        ? trackService.searchByTitle(title)
        : trackService.getAllTracks();
  }

  // 2. Поиск треков (специально для твоего запроса /api/tracks/search?name=Numb)
  @GetMapping("/tracks/search")
  public List<TrackDto> searchTracks(@RequestParam("name") String name) {
    return trackService.searchByTitle(name);
  }

  // 3. Добавление трека (POST /api/tracks) - ИСПРАВЛЯЕТ 405 ОШИБКУ
  @PostMapping("/tracks")
  @ResponseStatus(HttpStatus.CREATED)
  public TrackDto createTrack(@RequestBody TrackDto trackDto) {
    return trackService.save(trackDto);
  }

  // 4. Удаление трека (DELETE /api/tracks/{id}) - PathVariable
  @DeleteMapping("/tracks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    trackService.delete(id);
  }

  // === МЕТОДЫ ДЛЯ АРТИСТОВ И АЛЬБОМОВ ===

  @PostMapping("/artists")
  @ResponseStatus(HttpStatus.CREATED)
  public String createArtist(@RequestParam String name) {
    return artistService.saveArtist(name);
  }

  @PostMapping("/artists/{id}/albums")
  @ResponseStatus(HttpStatus.CREATED)
  public String addAlbum(@PathVariable Long id, @RequestParam String title) {
    return artistService.addAlbumToArtist(id, title);
  }

  @DeleteMapping("/artists/{id}")
  public String deleteArtist(@PathVariable Long id) {
    return artistService.deleteArtist(id);
  }

  @DeleteMapping("/albums/{id}")
  public String deleteAlbum(@PathVariable Long id) {
    return artistService.deleteAlbum(id);
  }

  @GetMapping("/test/n-plus-1")
  public String getStepN1() {
    return artistService.demonstrateNPlus1();
  }
}