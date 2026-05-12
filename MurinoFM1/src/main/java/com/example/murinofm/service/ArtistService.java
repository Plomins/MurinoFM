package com.example.murinofm.service;

import com.example.murinofm.cache.TrackSearchCache;
import com.example.murinofm.dto.ArtistDto;
import com.example.murinofm.entity.Artist;
import com.example.murinofm.entity.Album;
import com.example.murinofm.repository.ArtistRepository;
import com.example.murinofm.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

  private final ArtistRepository artistRepository;
  private final AlbumRepository albumRepository;
  private final TrackSearchCache trackSearchCache;

  @Transactional(readOnly = true)
  public List<String> getAllAlbumsList() {
    return albumRepository.findAll().stream()
        .map(album -> "Альбом: " + album.getTitle() + " [ID: " + album.getId() + "]")
        .toList();
  }

  @Transactional
  public String saveArtist(String name) {
    Artist artist = new Artist();
    artist.setName(name);
    Artist saved = artistRepository.save(artist);
    log.info("Создан артист: {} с ID: {}", name, saved.getId());
    trackSearchCache.invalidate();   // Инвалидация кеша
    return "Артист успешно создан! Его ID: " + saved.getId();
  }

  @Transactional
  public String deleteArtist(Long id) {
    if (!artistRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Артист не найден");
    }
    artistRepository.deleteById(id);
    String message = "Артист с ID " + id + " и все его альбомы удалены!";
    log.info(message);
    trackSearchCache.invalidate();   // Инвалидация кеша
    return message;
  }

  @Transactional
  public String deleteAlbum(Long id) {
    if (!albumRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Альбом не найден");
    }
    albumRepository.deleteById(id);
    trackSearchCache.invalidate();   // Инвалидация кеша
    return "Альбом успешно удален";
  }
  @Transactional
  public String addAlbumToArtist(Long artistId, String albumTitle) {
    Artist artist = artistRepository.findById(artistId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Артист не найден"));

    Album album = new Album();
    album.setTitle(albumTitle);
    album.setArtist(artist);

    albumRepository.save(album);
    log.info("Добавлен альбом '{}' артисту {}", albumTitle, artist.getName());
    trackSearchCache.invalidate();   // Инвалидация кеша
    return "Альбом '" + albumTitle + "' успешно добавлен артисту " + artist.getName();
  }

  @Transactional(readOnly = true)
  public List<String> getAllArtistsList() {
    return artistRepository.findAll().stream()
        .map(artist -> "Артист: " + artist.getName() + " [ID: " + artist.getId() + "]")
        .toList();
  }
}