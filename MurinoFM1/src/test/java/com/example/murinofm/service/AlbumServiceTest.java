package com.example.murinofm.service;

import com.example.murinofm.repository.AlbumRepository;
import com.example.murinofm.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

  @Mock private ArtistRepository artistRepository;
  @Mock private AlbumRepository albumRepository;

  @InjectMocks private AlbumService albumService;

  @Test
  void saveDataWithoutTransaction_NoError_Success() {
    albumService.saveDataWithoutTransaction(false);

    verify(artistRepository, times(1)).save(any());
    verify(albumRepository, times(1)).save(any());
  }

  @Test
  void saveDataWithoutTransaction_WithError_ThrowsException() {
    assertThrows(ResponseStatusException.class,
        () -> albumService.saveDataWithoutTransaction(true));

    verify(artistRepository, times(1)).save(any());
    verify(albumRepository, never()).save(any());
  }
}
