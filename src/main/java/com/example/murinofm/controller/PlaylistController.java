package com.example.murinofm.controller;

import com.example.murinofm.dto.PlaylistDto;
import com.example.murinofm.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

  private final PlaylistService playlistService;

  @PostMapping
  public String create(@RequestParam String name, @RequestBody List<Long> trackIds) {
    playlistService.createPlaylistWithTracks(name, trackIds);
    return "Плейлист '" + name + "' успешно создан";
  }
  @GetMapping("/{id}")
  public PlaylistDto getPlaylist(@PathVariable Long id) {
    return playlistService.getPlaylistById(id);
  }

}

