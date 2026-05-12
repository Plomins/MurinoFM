package com.example.murinofm.service;

import com.example.murinofm.cache.TrackSearchCache;
import com.example.murinofm.cache.TrackSearchKey;
import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.entity.Album;
import com.example.murinofm.entity.Playlist;
import com.example.murinofm.entity.Track;
import com.example.murinofm.exception.AppException;
import com.example.murinofm.repository.AlbumRepository;
import com.example.murinofm.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

  @Mock
  private TrackRepository trackRepository;

  @Mock
  private AlbumRepository albumRepository;

  @Mock
  private TrackSearchCache trackSearchCache;

  @InjectMocks
  private TrackService trackService;

  @Test
  void save_WithAlbumSuccess() {
    TrackDto dto = new TrackDto();
    dto.setTitle("Test Track");
    dto.setAlbumId(1L);

    Album album = new Album();
    album.setId(1L);

    Track track = new Track();
    track.setTitle("Test Track");
    track.setAlbum(album);
    when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
    when(trackRepository.save(any(Track.class))).thenReturn(track);

    TrackDto result = trackService.save(dto);

    assertNotNull(result);
    verify(trackSearchCache).invalidate();
  }

  @Test
  void delete_RemovesFromPlaylistsAndDeletes() {
    Track track = new Track();
    track.setId(1L);

    Playlist playlist = new Playlist();
    playlist.setTracks(new ArrayList<>(List.of(track)));
    track.setPlaylists(List.of(playlist));

    when(trackRepository.findById(1L)).thenReturn(Optional.of(track));

    trackService.delete(1L);
    assertFalse(playlist.getTracks().contains(track));
    verify(trackRepository).delete(track);
    verify(trackSearchCache).invalidate();
  }

  @Test
  void findByArtistName_CacheMiss() {
    Pageable pageable = PageRequest.of(0, 10);
    TrackSearchKey key = new TrackSearchKey("Artist", 0, 10);

    Track track = new Track();
    track.setTitle("Track");
    Page<Track> trackPage = new PageImpl<>(List.of(track));
    when(trackSearchCache.get(key)).thenReturn(null);
    when(trackRepository.findByArtistName("Artist", pageable)).thenReturn(trackPage);

    Page<TrackDto> result = trackService.findByArtistName("Artist", pageable);

    assertNotNull(result);
    verify(trackSearchCache).put(eq(key), any());
  }

  @Test
  void findByArtistName_CacheHit() {
    Pageable pageable = PageRequest.of(0, 10);
    TrackSearchKey key = new TrackSearchKey("Artist", 0, 10);

    Page<TrackDto> cachedPage = new PageImpl<>(List.of(new TrackDto()));
    when(trackSearchCache.get(key)).thenReturn(cachedPage);

    Page<TrackDto> result = trackService.findByArtistName("Artist", pageable);

    assertEquals(cachedPage, result);
    verify(trackRepository, never()).findByArtistName(anyString(), any());
  }
  @Test
  void save_WithoutAlbumSuccess() {
    TrackDto dto = new TrackDto();
    dto.setTitle("Test Track");
    dto.setAlbumId(null);

    Track track = new Track();
    track.setTitle("Test Track");

    when(trackRepository.save(any(Track.class))).thenReturn(track);

    TrackDto result = trackService.save(dto);

    assertNotNull(result);
    verify(albumRepository, never()).findById(anyLong());
  }

  @Test
  void save_AlbumNotFound_ThrowsException() {
    TrackDto dto = new TrackDto();
    dto.setAlbumId(1L);

    when(albumRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> trackService.save(dto));
  }

  @Test
  void getAllTracks_Success() {
    when(trackRepository.findAll()).thenReturn(List.of(new Track()));
    List<TrackDto> result = trackService.getAllTracks();
    assertFalse(result.isEmpty());
  }

  @Test
  void getTrackById_Success() {
    Track track = new Track();
    track.setTitle("Song");
    when(trackRepository.findById(1L)).thenReturn(Optional.of(track));

    TrackDto result = trackService.getTrackById(1L);
    assertEquals("Song", result.getTitle());
  }

  @Test
  void searchByTitle_Success() {
    when(trackRepository.findByTitleContainingIgnoreCase("Song"))
        .thenReturn(List.of(new Track()));

    List<TrackDto> result = trackService.searchByTitle("Song");
    assertFalse(result.isEmpty());
  }

  @Test
  void findByArtistNameNative_CacheMiss() {
    Pageable pageable = PageRequest.of(0, 10);
    TrackSearchKey key = new TrackSearchKey("Artist", 0, 10);

    Page<Track> trackPage = new PageImpl<>(List.of(new Track()));

    when(trackSearchCache.get(key)).thenReturn(null);
    when(trackRepository.findByArtistNameNative("Artist", pageable)).thenReturn(trackPage);

    Page<TrackDto> result = trackService.findByArtistNameNative("Artist", pageable);

    assertNotNull(result);
    verify(trackSearchCache).put(eq(key), any());
  }

  @Test
  void bulkCreate_Success() {
    TrackDto dto = new TrackDto();
    dto.setTitle("Bulk Track");

    when(trackRepository.save(any(Track.class))).thenReturn(new Track());

    List<TrackDto> result = trackService.bulkCreate(List.of(dto));
    assertFalse(result.isEmpty());
    verify(trackSearchCache).invalidate();
  }
  @Test
  void delete_TrackWithPlaylists_RemovesRelation() {
    Track track = new Track();
    track.setId(1L);
    Playlist playlist = spy(new Playlist());
    playlist.setTracks(new ArrayList<>(List.of(track)));
    track.setPlaylists(new ArrayList<>(List.of(playlist)));

    when(trackRepository.findById(1L)).thenReturn(Optional.of(track));

    trackService.delete(1L);
    assertFalse(playlist.getTracks().contains(track));
    verify(trackRepository).delete(track);
  }

  @Test
  void bulkCreate_AlbumNotFound_ThrowsAppException() {
    TrackDto dto = new TrackDto();
    dto.setAlbumId(999L);
    when(albumRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> trackService.bulkCreate(List.of(dto)));
  }
  @Test
  void findByArtistNameNative_CacheHit() {
    Pageable pageable = PageRequest.of(0, 10);
    TrackSearchKey key = new TrackSearchKey("Artist", 0, 10);
    Page<TrackDto> cachedPage = new PageImpl<>(List.of(new TrackDto()));

    when(trackSearchCache.get(key)).thenReturn(cachedPage);

    Page<TrackDto> result = trackService.findByArtistNameNative("Artist", pageable);

    assertEquals(cachedPage, result);
    verify(trackRepository, never()).findByArtistNameNative(any(), any());
  }

  @Test
  void bulkCreate_WithAlbumId_Present() {
    TrackDto dto = new TrackDto();
    dto.setAlbumId(1L);
    Album album = new Album();

    when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
    when(trackRepository.save(any())).thenReturn(new Track());
    trackService.bulkCreate(List.of(dto));

    verify(albumRepository).findById(1L);
  }
  @Test
  void getTrackById_NotFound_ThrowsException() {
    when(trackRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> trackService.getTrackById(1L));
  }

  @Test
  void delete_NotFound_ThrowsException() {
    when(trackRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> trackService.delete(1L));
  }
}