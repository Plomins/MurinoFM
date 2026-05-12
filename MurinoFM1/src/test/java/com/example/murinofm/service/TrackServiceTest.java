package com.example.murinofm.service;

import com.example.murinofm.cache.TrackSearchCache;
import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.entity.Album;
import com.example.murinofm.entity.Playlist;
import com.example.murinofm.entity.Track;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

  @Mock private TrackRepository trackRepository;
  @Mock private AlbumRepository albumRepository;
  @Mock private TrackSearchCache trackSearchCache;
  @InjectMocks private TrackService trackService;

  @Test
  void saveTrack_WithAlbum_Success() {
    TrackDto dto = new TrackDto();
    dto.setAlbumId(10L);
    when(albumRepository.findById(10L)).thenReturn(Optional.of(new Album()));
    when(trackRepository.save(any())).thenReturn(new Track());

    trackService.save(dto);
    verify(trackSearchCache).invalidate();
  }

  @Test
  void deleteTrack_ClearsPlaylists_Success() {
    Track track = new Track();
    Playlist playlist = new Playlist();
    playlist.setTracks(new ArrayList<>(List.of(track)));
    track.setPlaylists(List.of(playlist));

    when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
    trackService.delete(1L);

    assertTrue(playlist.getTracks().isEmpty());
    verify(trackRepository).delete(track);
    verify(trackSearchCache).invalidate();
  }

  @Test
  void getAllTracks_WithPageable() {
    PageRequest pageable = PageRequest.of(0, 10);
    when(trackRepository.findAll(pageable)).thenReturn(Page.empty());
    trackService.getAllTracks(pageable);
    verify(trackRepository).findAll(pageable);
  }

  @Test
  void findByArtistNameNative_CacheMiss() {
    PageRequest pageable = PageRequest.of(0, 10);
    when(trackSearchCache.get(any())).thenReturn(null);
    when(trackRepository.findByArtistNameNative(any(), any())).thenReturn(Page.empty());

    trackService.findByArtistNameNative("Artist", pageable);
    verify(trackSearchCache).put(any(), any());
  }

  @Test
  void bulkCreate_Success() {
    TrackDto dto = new TrackDto();
    dto.setTitle("Bulk");
    when(trackRepository.save(any())).thenReturn(new Track());

    trackService.bulkCreate(List.of(dto));
    verify(trackSearchCache).invalidate();
  }
  @Test
  void getTrackById_NotFound_ThrowsException() {
    when(trackRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> trackService.getTrackById(999L));
  }

  @Test
  void save_AlbumNotFound_ThrowsException() {
    TrackDto dto = new TrackDto();
    dto.setAlbumId(999L);
    when(albumRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> trackService.save(dto));
  }

  @Test
  void bulkCreate_AlbumNotFound_ThrowsException() {
    TrackDto dto = new TrackDto();
    dto.setAlbumId(999L);
    when(albumRepository.findById(999L)).thenReturn(Optional.empty());
    List<TrackDto> list = List.of(dto);
    assertThrows(IllegalArgumentException.class, () -> trackService.bulkCreate(list));
  }
  @Test
  void delete_NotFound_ThrowsException() {
    when(trackRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> trackService.delete(999L));
  }
  @Test
  void findByArtistName_CacheHit() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<TrackDto> cachedPage = new PageImpl<>(List.of(new TrackDto()));
    when(trackSearchCache.get(any())).thenReturn(cachedPage);

    Page<TrackDto> result = trackService.findByArtistName("Artist", pageable);

    assertEquals(1, result.getTotalElements());
    verify(trackRepository, never()).findByArtistName(any(), any());
  }

  @Test
  void findByArtistNameNative_CacheHit() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<TrackDto> cachedPage = new PageImpl<>(List.of(new TrackDto()));
    when(trackSearchCache.get(any())).thenReturn(cachedPage);

    Page<TrackDto> result = trackService.findByArtistNameNative("Artist", pageable);

    assertEquals(1, result.getTotalElements());
    verify(trackRepository, never()).findByArtistNameNative(any(), any());
  }
  @Test
  void getAllTracks_NoParams_Success() {
    when(trackRepository.findAll()).thenReturn(List.of(new Track()));
    List<TrackDto> result = trackService.getAllTracks();
    assertEquals(1, result.size());
  }

  @Test
  void searchByTitle_Success() {
    when(trackRepository.findByTitleContainingIgnoreCase("test")).thenReturn(List.of(new Track()));
    List<TrackDto> result = trackService.searchByTitle("test");
    assertEquals(1, result.size());
  }

  @Test
  void save_NoAlbumId_Success() {
    TrackDto dto = new TrackDto();
    dto.setTitle("No Album");
    when(trackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    TrackDto result = trackService.save(dto);
    assertNull(result.getAlbumId());
  }
  @Test
  void findByArtistName_CacheHit_Success() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<TrackDto> cachedPage = new PageImpl<>(List.of(new TrackDto()));
    when(trackSearchCache.get(any())).thenReturn(cachedPage);
    Page<TrackDto> result = trackService.findByArtistName("Some Artist", pageable);
    assertEquals(1, result.getTotalElements());
    verify(trackRepository, never()).findByArtistName(any(), any());
  }
  @Test
  void findByArtistNameNative_CacheHit_Coverage() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<TrackDto> cached = new PageImpl<>(List.of(new TrackDto()));
    when(trackSearchCache.get(any())).thenReturn(cached);

    var result = trackService.findByArtistNameNative("Artist", pageable);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void bulkCreate_EmptyList_Coverage() {
    assertTrue(trackService.bulkCreate(List.of()).isEmpty());
  }
  @Test
  void findByArtistName_CacheMiss_Success() {
    PageRequest pageable = PageRequest.of(0, 10);
    when(trackSearchCache.get(any())).thenReturn(null);
    when(trackRepository.findByArtistName(anyString(), any())).thenReturn(new PageImpl<>(List.of(new Track())));

    Page<TrackDto> result = trackService.findByArtistName("Artist", pageable);

    assertNotNull(result);
    verify(trackRepository).findByArtistName(anyString(), any());
    verify(trackSearchCache).put(any(), any());
  }

  @Test
  void bulkCreate_WithoutAlbumIds_Coverage() {

    TrackDto dto = new TrackDto();
    dto.setTitle("Single bulk track");
    dto.setDurationSeconds(100);
    dto.setAlbumId(null);

    when(trackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    List<TrackDto> result = trackService.bulkCreate(List.of(dto));
    assertEquals(1, result.size());
    assertNull(result.get(0).getAlbumId());
  }
  @Test
  void findByArtistName_CacheHit_FullCoverage() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<TrackDto> cachedPage = new PageImpl<>(List.of(new TrackDto()));

    when(trackSearchCache.get(any())).thenReturn(cachedPage);

    Page<TrackDto> result = trackService.findByArtistName("Artist", pageable);

    assertSame(cachedPage, result);
    verify(trackRepository, never()).findByArtistName(any(), any());
  }

  @Test
  void findByArtistNameNative_CacheHit_FullCoverage() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<TrackDto> cachedPage = new PageImpl<>(List.of(new TrackDto()));

    when(trackSearchCache.get(any())).thenReturn(cachedPage);

    Page<TrackDto> result = trackService.findByArtistNameNative("Artist", pageable);

    assertSame(cachedPage, result);
    verify(trackRepository, never()).findByArtistNameNative(any(), any());
  }

  @Test
  void bulkCreate_WithMixedAlbumPresence_Coverage() {
    TrackDto dtoWithoutAlbum = new TrackDto();
    dtoWithoutAlbum.setTitle("No Album");
    dtoWithoutAlbum.setAlbumId(null);

    when(trackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    List<TrackDto> result = trackService.bulkCreate(List.of(dtoWithoutAlbum));
    assertNull(result.get(0).getAlbumId());
    verify(trackSearchCache).invalidate();
  }
  @Test
  void getTrackById_Found_Success() {
    Track track = new Track();
    track.setId(1L);
    track.setTitle("Test Track");

    when(trackRepository.findById(1L)).thenReturn(Optional.of(track));

    TrackDto result = trackService.getTrackById(1L);
    assertNotNull(result);
  }
  @Test
  void bulkCreate_WithValidAlbum_Success() {
    TrackDto dto = new TrackDto();
    dto.setTitle("Track With Album");
    dto.setDurationSeconds(200);
    dto.setAlbumId(10L);

    Album album = new Album();
    when(albumRepository.findById(10L)).thenReturn(Optional.of(album));
    when(trackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    List<TrackDto> result = trackService.bulkCreate(List.of(dto));

    assertEquals(1, result.size());
    verify(albumRepository).findById(10L);
    verify(trackSearchCache).invalidate();
  }
}
