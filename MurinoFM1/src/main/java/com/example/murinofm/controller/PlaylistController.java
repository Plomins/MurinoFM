package com.example.murinofm.controller;

import com.example.murinofm.dto.PlaylistDto;
import com.example.murinofm.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

  private final PlaylistService playlistService;

  // Получение списка плейлистов с возможной фильтрацией по userId
  @GetMapping
  public List<PlaylistDto> getAll(@RequestParam(required = false) Long userId) {
    return playlistService.getAllPlaylists(userId);
  }

  // Получение одного плейлиста
  @GetMapping("/{id}")
  public PlaylistDto getPlaylist(@PathVariable Long id,
                                 @RequestParam(required = false) Long userId) {
    return playlistService.getPlaylistById(id, userId);
  }

  // Создание плейлиста для конкретного пользователя
  @PostMapping("/user/{userId}")
  public PlaylistDto createPlaylistForUser(
      @PathVariable Long userId,
      @RequestParam String name,
      @RequestParam(defaultValue = "true") boolean isPublic,
      @RequestBody(required = false) List<Long> trackIds) {
    return playlistService.createPlaylist(userId, name, trackIds, isPublic);
  }

  @PostMapping("/{playlistId}/tracks/{trackId}")
  public ResponseEntity<PlaylistDto> addTrackToPlaylist(
      @PathVariable Long playlistId,
      @PathVariable Long trackId,
      @RequestParam Long userId) {
    PlaylistDto playlist = playlistService.addTrackToPlaylist(playlistId, trackId, userId);
    return ResponseEntity.ok(playlist);
  }
  @Operation(summary = "Создать копию плейлиста для указанного пользователя")
  @PostMapping("/{id}/copy")
  public ResponseEntity<PlaylistDto> copyPlaylist(
      @PathVariable Long id,
      @RequestParam Long userId) {
    return ResponseEntity.ok(playlistService.copyPlaylist(id, userId));
  }
}