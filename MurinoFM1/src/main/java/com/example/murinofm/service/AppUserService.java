package com.example.murinofm.service;

import com.example.murinofm.aop.LogExecutionTime;
import com.example.murinofm.dto.AppUserDto;
import com.example.murinofm.entity.AppUser;
import com.example.murinofm.entity.Playlist;
import com.example.murinofm.exception.AppException;
import com.example.murinofm.exception.ConflictException;
import com.example.murinofm.repository.AppUserRepository;
import com.example.murinofm.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

  private static final String USER_NOT_FOUND = "Пользователь с ID ";
  private static final String NOT_FOUND_SUFFIX = " не найден";

  private final AppUserRepository appUserRepository;
  private final PlaylistRepository playlistRepository;

  @Transactional(readOnly = true)
  public List<AppUserDto> getAllUsers() {
    return appUserRepository.findAll().stream()
        .map(AppUserDto::fromEntity)
        .toList();
  }

  @Transactional(readOnly = true)
  public AppUserDto getUserById(Long id) {
    AppUser user = appUserRepository.findById(id)
        .orElseThrow(() -> new AppException(USER_NOT_FOUND + id + NOT_FOUND_SUFFIX));
    return AppUserDto.fromEntity(user);
  }

  @LogExecutionTime
  @Transactional
  public AppUserDto createUser(String username, String password) {
    if (appUserRepository.existsByUsername(username)) {
      throw new ConflictException("Пользователь с именем '" + username + "' уже существует");
    }
    AppUser user = new AppUser();
    user.setUsername(username);
    user.setPassword(password);   // в реальном проекте нужно хешировать
    AppUser saved = appUserRepository.save(user);
    log.info("Создан пользователь: {} с ID: {}", username, saved.getId());
    return AppUserDto.fromEntity(saved);
  }

  // Вход – проверка имени и пароля
  @Transactional(readOnly = true)
  public AppUserDto authenticate(String username, String password) {
    AppUser user = appUserRepository.findAll().stream()
        .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
        .findFirst()
        .orElseThrow(() -> new AppException("Неверное имя пользователя или пароль"));
    return AppUserDto.fromEntity(user);
  }

  @Transactional
  public AppUserDto updateUsername(Long id, String newUsername) {
    AppUser user = appUserRepository.findById(id)
        .orElseThrow(() -> new AppException(USER_NOT_FOUND + id + NOT_FOUND_SUFFIX));
    user.setUsername(newUsername);
    return AppUserDto.fromEntity(appUserRepository.save(user));
  }

  @Transactional
  public void deleteUser(Long id) {
    if (!appUserRepository.existsById(id)) {
      throw new AppException(USER_NOT_FOUND + id + NOT_FOUND_SUFFIX);
    }
    appUserRepository.deleteById(id);
    log.info("Удалён пользователь с ID: {}", id);
  }

  @Transactional
  public AppUserDto addPlaylistToUser(Long userId, Long playlistId) {
    AppUser user = appUserRepository.findById(userId)
        .orElseThrow(() -> new AppException(USER_NOT_FOUND + userId + NOT_FOUND_SUFFIX));
    Playlist playlist = playlistRepository.findById(playlistId)
        .orElseThrow(() -> new AppException("Плейлист с ID " + playlistId + NOT_FOUND_SUFFIX));
    playlist.setOwner(user);
    user.getPlaylists().add(playlist);
    return AppUserDto.fromEntity(appUserRepository.save(user));
  }
  @Transactional
  public AppUserDto updateAvatar(Long id, String avatarUrl) {
    AppUser user = appUserRepository.findById(id)
        .orElseThrow(() -> new AppException("Пользователь не найден"));
    user.setAvatarUrl(avatarUrl);
    return AppUserDto.fromEntity(appUserRepository.save(user));
  }
  public List<AppUserDto> searchUsers(String query) {
    return appUserRepository.findByUsernameContainingIgnoreCase(query)
        .stream()
        .map(AppUserDto::fromEntity)
        .toList();
  }
}
