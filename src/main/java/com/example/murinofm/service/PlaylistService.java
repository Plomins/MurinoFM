
package com.example.murinofm.service;

import com.example.murinofm.dto.PlaylistDto;
import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.entity.Playlist;
import com.example.murinofm.entity.Track;
import com.example.murinofm.repository.PlaylistRepository;
import com.example.murinofm.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

  private final PlaylistRepository playlistRepository;
  private final TrackRepository trackRepository;

  @Transactional
  public void createPlaylistWithTracks(String name, List<Long> trackIds) {
    Playlist playlist = new Playlist();
    playlist.setName(name);

    // Та самая проверка "от препода"
    if (trackIds == null || trackIds.isEmpty()) {
      throw new RuntimeException("Невалидные данные: плейлист не может быть пустым!");
    }

    List<Track> tracks = trackRepository.findAllById(trackIds);

    if (tracks.size() != trackIds.size()) {
      throw new RuntimeException("Ошибка: часть треков не найдена. Откат транзакции!");
    }

    playlist.setTracks(tracks);
    playlistRepository.save(playlist);
  }
  @Transactional(readOnly = true)
  public PlaylistDto getPlaylistById(Long id) {
    Playlist playlist = playlistRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Плейлист с ID " + id + " не найден"));

    List<TrackDto> trackDtos = playlist.getTracks().stream()
        .map(TrackDto::fromEntity)
        .toList();

    return new PlaylistDto(playlist.getId(), playlist.getName(), trackDtos);
  }
}