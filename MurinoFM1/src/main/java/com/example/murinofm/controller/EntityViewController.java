package com.example.murinofm.controller;

import com.example.murinofm.dto.*;
import com.example.murinofm.entity.Album;
import com.example.murinofm.entity.Artist;
import com.example.murinofm.repository.AlbumRepository;
import com.example.murinofm.repository.ArtistRepository;
import com.example.murinofm.service.AppUserService;
import com.example.murinofm.service.PlaylistService;
import com.example.murinofm.service.TrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/view")
@RequiredArgsConstructor
@Tag(name = "Просмотр сущностей", description = "Прямой доступ к артистам, альбомам, трекам, пользователям (только чтение)")
public class EntityViewController {

  private final ArtistRepository artistRepository;
  private final AlbumRepository albumRepository;
  private final TrackService trackService;
  private final PlaylistService playlistService;
  private final AppUserService appUserService;

  @Operation(summary = "Все артисты")
  @GetMapping("/artists")
  public List<ArtistDto> getAllArtists() {
    return artistRepository.findAll().stream()
        .map(ArtistDto::fromEntity)
        .toList();
  }

  @Operation(summary = "Артист по ID")
  @GetMapping("/artists/{id}")
  public ArtistDto getArtistById(@PathVariable Long id) {
    Artist artist = artistRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Артист с ID " + id + " не найден"));
    return ArtistDto.fromEntity(artist);
  }

  @Operation(summary = "Все альбомы (с треками)")
  @GetMapping("/albums")
  public List<AlbumDto> getAllAlbums() {
    return albumRepository.findAllWithTracks().stream()
        .map(AlbumDto::fromEntity)
        .toList();
  }

  @Operation(summary = "Альбом по ID")
  @GetMapping("/albums/{id}")
  public AlbumDto getAlbumById(@PathVariable Long id) {
    Album album = albumRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Альбом с ID " + id + " не найден"));
    return AlbumDto.fromEntity(album);
  }

  @Operation(summary = "Все треки")
  @GetMapping("/tracks")
  public List<TrackDto> getAllTracks() {
    return trackService.getAllTracks();
  }

  @Operation(summary = "Трек по ID")
  @GetMapping("/tracks/{id}")
  public TrackDto getTrackById(@PathVariable Long id) {
    return trackService.getTrackById(id);
  }

  @Operation(summary = "Плейлист по ID")
  @GetMapping("/playlists/{id}")
  public PlaylistDto getPlaylistById(@PathVariable Long id) {
    return playlistService.getPlaylistById(id, null);
  }

  @Operation(summary = "Все пользователи")
  @GetMapping("/users")
  public List<AppUserDto> getAllUsers() {
    return appUserService.getAllUsers();
  }

  @Operation(summary = "Пользователь по ID")
  @GetMapping("/users/{id}")
  public AppUserDto getUserById(@PathVariable Long id) {
    return appUserService.getUserById(id);
  }

  @Operation(summary = "Все сущности сразу")
  @GetMapping("/all")
  public Map<String, Object> getAllEntities() {
    return Map.of(
        "artists", getAllArtists(),
        "albums", getAllAlbums(),
        "tracks", trackService.getAllTracks(),
        "users", appUserService.getAllUsers()
    );
  }
}