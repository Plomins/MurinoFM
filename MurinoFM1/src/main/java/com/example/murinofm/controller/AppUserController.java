package com.example.murinofm.controller;

import com.example.murinofm.dto.AppUserDto;
import com.example.murinofm.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление пользователями и их плейлистами")
public class AppUserController {

  private final AppUserService appUserService;

  @Operation(summary = "Поиск пользователей по имени")
  @GetMapping("/search")
  public List<AppUserDto> searchUsers(@RequestParam String q) {
    return appUserService.searchUsers(q);
  }

  @Operation(summary = "Получить всех пользователей")
  @GetMapping
  public List<AppUserDto> getAllUsers() {
    return appUserService.getAllUsers();
  }

  @Operation(summary = "Получить пользователя по ID")
  @GetMapping("/{id}")
  public AppUserDto getUserById(
      @Parameter(description = "ID пользователя") @PathVariable Long id) {
    return appUserService.getUserById(id);
  }

  @Operation(summary = "Создать пользователя")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AppUserDto createUser(
      @Parameter(description = "Имя пользователя") @RequestParam String username,
      @Parameter(description = "Пароль") @RequestParam String password) {
    return appUserService.createUser(username, password);
  }

  @Operation(summary = "Обновить имя пользователя")
  @PutMapping("/{id}")
  public AppUserDto updateUser(
      @Parameter(description = "ID пользователя") @PathVariable Long id,
      @Parameter(description = "Новое имя") @RequestParam String username) {
    return appUserService.updateUsername(id, username);
  }

  @Operation(summary = "Удалить пользователя")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(
      @Parameter(description = "ID пользователя") @PathVariable Long id) {
    appUserService.deleteUser(id);
  }

  @Operation(summary = "Добавить существующий плейлист пользователю")
  @PostMapping("/{userId}/playlists/{playlistId}")
  public AppUserDto addPlaylist(
      @Parameter(description = "ID пользователя") @PathVariable Long userId,
      @Parameter(description = "ID плейлиста") @PathVariable Long playlistId) {
    return appUserService.addPlaylistToUser(userId, playlistId);
  }
}