package com.example.murinofm.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.murinofm.cache.TrackSearchCache;
import com.example.murinofm.cache.TrackSearchKey;
import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.entity.Album;
import com.example.murinofm.entity.Playlist;
import com.example.murinofm.entity.Track;
import com.example.murinofm.exception.AppException;
import com.example.murinofm.exception.BulkOperationException;
import com.example.murinofm.repository.AlbumRepository;
import com.example.murinofm.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrackService {

  private final TrackRepository trackRepository;
  private final AlbumRepository albumRepository;
  private final TrackSearchCache trackSearchCache;
  @Transactional
  public TrackDto save(TrackDto dto) {
    Track track = new Track();
    track.setTitle(dto.getTitle());
    track.setDurationSeconds(dto.getDurationSeconds());
    track.setAudioUrl(dto.getAudioUrl());
    track.setCoverImageUrl(dto.getCoverImageUrl());   // <-- вот эта строка важна

    if (dto.getAlbumId() != null) {
      Album album = albumRepository.findById(dto.getAlbumId())
          .orElseThrow(() -> new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Альбом с ID " + dto.getAlbumId() + " не найден"));
      track.setAlbum(album);
    }
    Track savedTrack = trackRepository.save(track);
    trackSearchCache.invalidate();
    return TrackDto.fromEntity(savedTrack);
  }
  @Transactional(readOnly = true)
  public List<TrackDto> getAllTracks() {
    return trackRepository.findAll().stream()
        .map(TrackDto::fromEntity)
        .toList();
  }
  public Page<TrackDto> getAllTracks(Pageable pageable) {
    return trackRepository.findAll(pageable).map(TrackDto::fromEntity);
  }
  @Transactional(readOnly = true)
  public TrackDto getTrackById(Long id) {
    return trackRepository.findById(id)
        .map(TrackDto::fromEntity)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found"));
  }

  @Transactional(readOnly = true)
  public List<TrackDto> searchByTitle(String title) {
    return trackRepository.findByTitleContainingIgnoreCase(title).stream()
        .map(TrackDto::fromEntity)
        .toList();
  }

  @Transactional
  public void delete(Long id) {
    Track track = trackRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Трек не найден"));
    for (Playlist playlist : track.getPlaylists()) {
      playlist.getTracks().remove(track);
    }
    trackRepository.delete(track);
    trackSearchCache.invalidate();
  }

  @Transactional(readOnly = true)
  public Page<TrackDto> findByArtistName(String artistName, Pageable pageable) {
    TrackSearchKey key = new TrackSearchKey(artistName, pageable.getPageNumber(), pageable.getPageSize());
    Page<TrackDto> cached = trackSearchCache.get(key);
    if (cached != null) {
      return cached;
    }
    Page<Track> trackPage = trackRepository.findByArtistName(artistName, pageable);
    Page<TrackDto> dtoPage = trackPage.map(TrackDto::fromEntity);
    trackSearchCache.put(key, dtoPage);
    return dtoPage;
  }

  @Transactional(readOnly = true)
  public Page<TrackDto> findByArtistNameNative(String artistName, Pageable pageable) {
    TrackSearchKey key = new TrackSearchKey(artistName, pageable.getPageNumber(), pageable.getPageSize());
    Page<TrackDto> cached = trackSearchCache.get(key);
    if (cached != null) {
      return cached;
    }
    Page<Track> trackPage = trackRepository.findByArtistNameNative(artistName, pageable);
    Page<TrackDto> dtoPage = trackPage.map(TrackDto::fromEntity);
    trackSearchCache.put(key, dtoPage);
    return dtoPage;
  }
  //@Transactional
  public List<TrackDto> bulkCreate(List<TrackDto> dtos) {
    List<Track> saved = new ArrayList<>();
    for (TrackDto dto : dtos) {
      Track track = new Track();
      track.setTitle(dto.getTitle());
      track.setDurationSeconds(dto.getDurationSeconds());

      if (dto.getAlbumId() != null) {
        Album album = albumRepository.findById(dto.getAlbumId())
            .orElseThrow(() -> new IllegalArgumentException("Альбом с ID " + dto.getAlbumId() + " не найден"));
        track.setAlbum(album);
      }

      saved.add(trackRepository.save(track));
    }
    trackSearchCache.invalidate();
    return saved.stream().map(TrackDto::fromEntity).collect(Collectors.toList());
  }
}