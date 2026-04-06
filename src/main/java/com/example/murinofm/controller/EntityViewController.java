package com.example.murinofm.controller;

import com.example.murinofm.dto.AppUserDto;
import com.example.murinofm.dto.PlaylistDto;
import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.entity.Album;
import com.example.murinofm.entity.Artist;
import com.example.murinofm.repository.AlbumRepository;
import com.example.murinofm.repository.ArtistRepository;
import com.example.murinofm.service.AppUserService;
import com.example.murinofm.service.PlaylistService;
import com.example.murinofm.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/view")
@RequiredArgsConstructor
public class EntityViewController {

  private final ArtistRepository artistRepository;
  private final AlbumRepository albumRepository;
  private final TrackService trackService;
  private final PlaylistService playlistService;
  private final AppUserService appUserService;

  @GetMapping("/artists")
  public List<Artist> getAllArtists() {
    return artistRepository.findAll();
  }

  @GetMapping("/artists/{id}")
  public Artist getArtistById(@PathVariable Long id) {
    return artistRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Артист с ID " + id + " не найден"));
  }

  @GetMapping("/albums")
  public List<Album> getAllAlbums() {
    return albumRepository.findAllWithTracks();
  }


  @GetMapping("/albums/{id}")
  public Album getAlbumById(@PathVariable Long id) {
    return albumRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Альбом с ID " + id + " не найден"));
  }
  @GetMapping("/tracks")
  public List<TrackDto> getAllTracks() {
    return trackService.getAllTracks();
  }

  @GetMapping("/tracks/{id}")
  public TrackDto getTrackById(@PathVariable Long id) {
    return trackService.getTrackById(id);
  }

  @GetMapping("/playlists/{id}")
  public PlaylistDto getPlaylistById(@PathVariable Long id) {
    return playlistService.getPlaylistById(id);
  }

  @GetMapping("/users")
  public List<AppUserDto> getAllUsers() {
    return appUserService.getAllUsers();
  }

  @GetMapping("/users/{id}")
  public AppUserDto getUserById(@PathVariable Long id) {
    return appUserService.getUserById(id);
  }

  @GetMapping("/all")
  public Map<String, Object> getAllEntities() {
    return Map.of(
        "artists",   artistRepository.findAll(),
        "albums",    albumRepository.findAllWithTracks(),
        "tracks",    trackService.getAllTracks(),
        "users",     appUserService.getAllUsers()
    );
  }
}