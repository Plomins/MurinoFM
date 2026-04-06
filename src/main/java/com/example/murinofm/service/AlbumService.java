package com.example.murinofm.service;

import com.example.murinofm.entity.Album;
import com.example.murinofm.entity.Artist;
import com.example.murinofm.repository.AlbumRepository;
import com.example.murinofm.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AlbumService {

  private final ArtistRepository artistRepository;
  private final AlbumRepository albumRepository;

  public void saveDataWithoutTransaction(boolean throwError) {
    Artist artist = new Artist();
    artist.setName("Сохранение БЕЗ транзакции");
    artistRepository.save(artist);

    if (throwError) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Внезапная ошибка без транзакции!");
    }

    Album album = new Album();
    album.setTitle("новый альбом созданный БЕЗ транзакции");
    album.setArtist(artist);
    albumRepository.save(album);
  }

}