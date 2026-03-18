package com.example.murinofm.service;

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

  @Transactional
  public String saveArtist(String name) {
    Artist artist = new Artist();
    artist.setName(name);
    Artist saved = artistRepository.save(artist);
    log.info("Создан артист: {} с ID: {}", name, saved.getId());
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
    return message;
  }
  @Transactional
  public String deleteAlbum(Long id) {
    if (!albumRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Альбом не найден");
    }
    albumRepository.deleteById(id);
    return "Альбом успешно удален";
  }
  @Transactional(readOnly = true)
  public String demonstrateNPlus1() {
    // 1. Первый запрос: Получаем всех артистов
    List<Artist> artists = artistRepository.findAll();

    StringBuilder report = new StringBuilder("=== Тест N+1 завершен ===\n");

    // 2. N запросов: Для каждого артиста лениво подгружаем альбомы
    for (Artist artist : artists) {
      int count = artist.getAlbums().size(); // Тут Hibernate идет в базу за альбомами конкретного артиста
      report.append("Артист: ").append(artist.getName())
          .append(" | Альбомов в базе: ").append(count).append("\n");
    }

    return report.toString() + "Смотри SQL-логи в консоли IntelliJ IDEA!";
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
    return "Альбом '" + albumTitle + "' успешно добавлен артисту " + artist.getName();
  }
}