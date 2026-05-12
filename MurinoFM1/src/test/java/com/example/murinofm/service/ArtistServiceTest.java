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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

  @Mock private ArtistRepository artistRepository;
  @Mock private AlbumRepository albumRepository;
  @Mock private TrackSearchCache trackSearchCache;

  @InjectMocks private ArtistService artistService;

  @Test
  void saveArtist_Success() {
    when(artistRepository.save(any())).thenReturn(new Artist());
    artistService.saveArtist("New Artist");
    verify(trackSearchCache).invalidate();
  }

  @Test
  void deleteArtist_NotFound_ThrowsException() {
    when(artistRepository.existsById(1L)).thenReturn(false);
    assertThrows(ResponseStatusException.class, () -> artistService.deleteArtist(1L));
  }

  @Test
  void addAlbumToArtist_Success() {
    Artist artist = new Artist();
    artist.setName("Artist Name");
    when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

    artistService.addAlbumToArtist(1L, "New Album");

    verify(albumRepository).save(any());
    verify(trackSearchCache).invalidate();
  }

  @Test
  void getAllArtistsList_FormattingCheck() {
    Artist artist = new Artist(); artist.setId(1L); artist.setName("Test");
    when(artistRepository.findAll()).thenReturn(List.of(artist));

    List<String> result = artistService.getAllArtistsList();
    assertEquals("Артист: Test [ID: 1]", result.get(0));
  }
  @Test
  void deleteAlbum_Success() {
    when(albumRepository.existsById(1L)).thenReturn(true);
    artistService.deleteAlbum(1L);
    verify(albumRepository).deleteById(1L);
    verify(trackSearchCache).invalidate();
  }

  @Test
  void getAllAlbumsList_Success() {
    Album album = new Album(); album.setId(1L); album.setTitle("Title");
    when(albumRepository.findAll()).thenReturn(List.of(album));
    List<String> result = artistService.getAllAlbumsList();
    assertTrue(result.get(0).contains("Title"));
  }
  @Test
  void deleteAlbum_NotFound_ThrowsException() {
    when(albumRepository.existsById(1L)).thenReturn(false);
    assertThrows(ResponseStatusException.class, () -> artistService.deleteAlbum(1L));
  }

  @Test
  void addAlbumToArtist_ArtistNotFound_ThrowsException() {
    when(artistRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> artistService.addAlbumToArtist(1L, "Title"));
  }
  @Test
  void deleteArtist_Success() {
    when(artistRepository.existsById(1L)).thenReturn(true);

    String result = artistService.deleteArtist(1L);

    verify(artistRepository).deleteById(1L);
    verify(trackSearchCache).invalidate();
    assertTrue(result.contains("удалены"));
  }
}
