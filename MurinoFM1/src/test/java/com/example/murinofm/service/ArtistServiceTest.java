package com.example.murinofm.service;

import com.example.murinofm.cache.TrackSearchCache;
import com.example.murinofm.entity.Album;
import com.example.murinofm.entity.Artist;
import com.example.murinofm.repository.AlbumRepository;
import com.example.murinofm.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private AlbumRepository albumRepository;

  @Mock
  private TrackSearchCache trackSearchCache;

  @InjectMocks
  private ArtistService artistService;

  @Test
  void saveArtist_Success() {
    Artist artist = new Artist();
    artist.setId(1L);
    artist.setName("Test Artist");

    when(artistRepository.save(any(Artist.class))).thenReturn(artist);

    String result = artistService.saveArtist("Test Artist");

    assertTrue(result.contains("Артист успешно создан!"));
    verify(trackSearchCache, times(1)).invalidate();
  }

  @Test
  void deleteArtist_ThrowsNotFound() {
    when(artistRepository.existsById(1L)).thenReturn(false);
    assertThrows(ResponseStatusException.class, () -> artistService.deleteArtist(1L));
    verify(trackSearchCache, never()).invalidate();
  }

  @Test
  void addAlbumToArtist_Success() {
    Artist artist = new Artist();
    artist.setId(1L);
    artist.setName("Artist");

    when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

    String result = artistService.addAlbumToArtist(1L, "New Album");

    assertTrue(result.contains("успешно добавлен"));
    verify(albumRepository).save(any(Album.class));
    verify(trackSearchCache).invalidate();
  }
  @Test
  void getAllAlbumsList_Success() {
    Album album = new Album();
    album.setId(1L);
    album.setTitle("Test Album");

    when(albumRepository.findAll()).thenReturn(List.of(album));

    List<String> result = artistService.getAllAlbumsList();

    assertFalse(result.isEmpty());
    assertTrue(result.get(0).contains("Test Album"));
  }

  @Test
  void getAllArtistsList_Success() {
    Artist artist = new Artist();
    artist.setId(1L);
    artist.setName("Test Artist");

    when(artistRepository.findAll()).thenReturn(List.of(artist));

    List<String> result = artistService.getAllArtistsList();

    assertFalse(result.isEmpty());
    assertTrue(result.get(0).contains("Test Artist"));
  }

  @Test
  void deleteArtist_Success() {
    when(artistRepository.existsById(1L)).thenReturn(true);

    String result = artistService.deleteArtist(1L);

    assertTrue(result.contains("удалены"));
    verify(artistRepository).deleteById(1L);
    verify(trackSearchCache).invalidate();
  }

  @Test
  void deleteAlbum_Success() {
    when(albumRepository.existsById(1L)).thenReturn(true);
    String result = artistService.deleteAlbum(1L);

    assertTrue(result.contains("успешно удален"));
    verify(albumRepository).deleteById(1L);
    verify(trackSearchCache).invalidate();}
  @Test
  void addAlbumToArtist_ArtistNotFound_ThrowsException() {
    when(artistRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> artistService.addAlbumToArtist(1L, "Title"));
  }
  @Test
  void deleteAlbum_Success_InvalidatesCache() {
    when(albumRepository.existsById(1L)).thenReturn(true);

    String result = artistService.deleteAlbum(1L);

    assertEquals("Альбом успешно удален", result);
    verify(albumRepository).deleteById(1L);
    verify(trackSearchCache).invalidate();
  }
  @Test
  void deleteAlbum_NotFound_ThrowsException() {
    when(albumRepository.existsById(1L)).thenReturn(false);

    assertThrows(ResponseStatusException.class, () ->
        artistService.deleteAlbum(1L));
  }
}