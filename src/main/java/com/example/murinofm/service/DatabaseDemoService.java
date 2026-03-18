package com.example.murinofm.service;

import com.example.murinofm.entity.Album;
import com.example.murinofm.entity.Artist;
import com.example.murinofm.repository.AlbumRepository;
import com.example.murinofm.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DatabaseDemoService {

  private final ArtistRepository artistRepository;
  private final AlbumRepository albumRepository;


  public void saveDataWithoutTransaction(boolean throwError) {
    Artist artist = new Artist();
    artist.setName("Linkin Park");
    artistRepository.save(artist);

    if (throwError) {
      throw new RuntimeException("Упс! Ошибка после сохранения артиста, но до альбома.");
    }

    Album album = new Album();
    album.setTitle("Meteora");
    album.setArtist(artist);
    albumRepository.save(album);
  }

  @Transactional
  public void saveDataWithTransaction(boolean throwError) {
    Artist artist = new Artist();
    artist.setName("Daft Punk");
    artistRepository.save(artist);

    if (throwError) {
      throw new RuntimeException("Ошибка! Благодаря @Transactional артист НЕ сохранится.");
    }

    Album album = new Album();
    album.setTitle("Discovery");
    album.setArtist(artist);
    albumRepository.save(album);
  }
}