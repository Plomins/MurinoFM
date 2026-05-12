package com.example.murinofm.service;

import com.example.murinofm.dto.PlaylistDto;
import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.entity.AppUser;
import com.example.murinofm.entity.Playlist;
import com.example.murinofm.entity.Track;
import com.example.murinofm.exception.AppException;
import com.example.murinofm.repository.AppUserRepository;
import com.example.murinofm.repository.PlaylistRepository;
import com.example.murinofm.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

  private final PlaylistRepository playlistRepository;
  private final TrackRepository trackRepository;
  private final AppUserRepository appUserRepository;

  @Transactional
  public void createMultiplePlaylists(List<PlaylistDto> requests) {
    // оставьте как есть или удалите, если не используется
  }

  @Transactional(readOnly = true)
  public PlaylistDto getPlaylistById(Long id, Long userId) {
    Playlist playlist = playlistRepository.findById(id)
        .orElseThrow(() -> new AppException("Плейлист с ID " + id + " не найден"));

    // Проверка доступа: публичный либо владелец
    if (!playlist.isPublic() && (userId == null || !playlist.getOwner().getId().equals(userId))) {
      throw new AppException("Нет доступа к этому плейлисту");
    }
    return PlaylistDto.fromEntity(playlist);
  }

  @Transactional(readOnly = true)
  public List<PlaylistDto> getAllPlaylists(Long userId) {
    List<Playlist> playlists = playlistRepository.findAll();

    if (userId != null) {
      // авторизован: публичные + свои приватные
      return playlists.stream()
          .filter(p -> p.isPublic() || p.getOwner().getId().equals(userId))
          .map(PlaylistDto::fromEntity)
          .toList();
    } else {
      // неавторизован: только публичные
      return playlists.stream()
          .filter(Playlist::isPublic)
          .map(PlaylistDto::fromEntity)
          .toList();
    }
  }

  @Transactional
  public PlaylistDto createPlaylist(Long userId, String name, List<Long> trackIds, boolean isPublic) {
    AppUser user = appUserRepository.findById(userId)
        .orElseThrow(() -> new AppException("Пользователь не найден"));
    Playlist playlist = new Playlist();
    playlist.setName(name);
    playlist.setOwner(user);
    playlist.setPublic(isPublic);
    if (trackIds != null && !trackIds.isEmpty()) {
      List<Track> tracks = trackRepository.findAllById(trackIds);
      playlist.setTracks(tracks);
    }
    return PlaylistDto.fromEntity(playlistRepository.save(playlist));
  }

  @Transactional
  public PlaylistDto addTrackToPlaylist(Long playlistId, Long trackId, Long userId) {
    Playlist playlist = playlistRepository.findById(playlistId)
        .orElseThrow(() -> new AppException("Плейлист не найден"));
    // Только владелец может добавлять треки
    if (!playlist.getOwner().getId().equals(userId)) {
      throw new AppException("Только владелец может добавлять треки в плейлист");
    }
    Track track = trackRepository.findById(trackId)
        .orElseThrow(() -> new AppException("Трек не найден"));
    if (!playlist.getTracks().contains(track)) {
      playlist.getTracks().add(track);
    }
    return PlaylistDto.fromEntity(playlistRepository.save(playlist));
  }
  @Transactional
  public PlaylistDto copyPlaylist(Long id, Long userId) {
    Playlist original = playlistRepository.findById(id)
        .orElseThrow(() -> new AppException("Плейлист не найден"));
    AppUser user = appUserRepository.findById(userId)
        .orElseThrow(() -> new AppException("Пользователь не найден"));
    Playlist copy = new Playlist();
    copy.setName(original.getName() + " (копия)");
    copy.setOwner(user);
    copy.setPublic(false);
    copy.setTracks(new ArrayList<>(original.getTracks()));
    return PlaylistDto.fromEntity(playlistRepository.save(copy));
  }
}