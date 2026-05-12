package com.example.murinofm.service;

import com.example.murinofm.dto.PlaylistDto;
import com.example.murinofm.entity.Playlist;
import com.example.murinofm.entity.Track;
import com.example.murinofm.exception.AppException;
import com.example.murinofm.repository.PlaylistRepository;
import com.example.murinofm.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

  @Mock
  private PlaylistRepository playlistRepository;

  @Mock
  private TrackRepository trackRepository;

  @InjectMocks
  private PlaylistService playlistService;

  @Test
  void createMultiplePlaylists_Success() {
    PlaylistDto dto = new PlaylistDto(null, "My Playlist", null);
    dto.setTrackIds(List.of(1L, 2L));

    Track track1 = new Track(); track1.setId(1L);
    Track track2 = new Track(); track2.setId(2L);

    when(trackRepository.findAllById(dto.getTrackIds())).thenReturn(List.of(track1, track2));

    playlistService.createMultiplePlaylists(List.of(dto));
    verify(playlistRepository).save(any(Playlist.class));
  }

  @Test
  void createMultiplePlaylists_EmptyTrackIdsThrowsException() {
    PlaylistDto dto = new PlaylistDto(null, "Empty Playlist", null);
    dto.setTrackIds(List.of());
    assertThrows(AppException.class, () -> playlistService.createMultiplePlaylists(List.of(dto)));
  }

  @Test
  void createMultiplePlaylists_MissingTracksThrowsException() {
    PlaylistDto dto = new PlaylistDto(null, "Playlist", null);
    dto.setTrackIds(List.of(1L, 2L));

    Track track1 = new Track(); track1.setId(1L);
    when(trackRepository.findAllById(dto.getTrackIds())).thenReturn(List.of(track1));
    assertThrows(AppException.class, () -> playlistService.createMultiplePlaylists(List.of(dto)));
  }
  @Test
  void getPlaylistById_Success() {
    Playlist playlist = new Playlist();
    playlist.setId(1L);
    playlist.setName("My Playlist");
    playlist.setTracks(List.of());

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

    PlaylistDto result = playlistService.getPlaylistById(1L);

    assertEquals("My Playlist", result.getName());
  }

  @Test
  void getPlaylistById_NotFound_ThrowsException() {
    when(playlistRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> playlistService.getPlaylistById(1L));
  }

  @Test
  void getAllPlaylists_Success() {
    Playlist playlist = new Playlist();
    playlist.setId(1L);
    playlist.setName("My Playlist");
    playlist.setTracks(List.of());

    when(playlistRepository.findAll()).thenReturn(List.of(playlist));

    List<PlaylistDto> result = playlistService.getAllPlaylists();

    assertFalse(result.isEmpty());
    assertEquals("My Playlist", result.get(0).getName());
  }
  @Test
  void createMultiplePlaylists_NullTrackIds_ThrowsException() {
    PlaylistDto dto = new PlaylistDto();
    dto.setName("Null Playlist");
    dto.setTrackIds(null);

    AppException ex = assertThrows(AppException.class,
        () -> playlistService.createMultiplePlaylists(List.of(dto)));
    assertTrue(ex.getMessage().contains("не может быть пустым"));
  }
  @Test
  void createMultiplePlaylists_TrackIdsNull_ThrowsException() {
    PlaylistDto dto = new PlaylistDto();
    dto.setName("Null Playlist");
    dto.setTrackIds(null);

    assertThrows(AppException.class, () ->
        playlistService.createMultiplePlaylists(List.of(dto)));
  }
}