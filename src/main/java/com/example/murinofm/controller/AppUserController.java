package com.example.murinofm.controller;

import com.example.murinofm.dto.AppUserDto;
import com.example.murinofm.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

  private final AppUserService appUserService;

  @GetMapping
  public List<AppUserDto> getAllUsers() {
    return appUserService.getAllUsers();
  }

  @GetMapping("/{id}")
  public AppUserDto getUserById(@PathVariable Long id) {
    return appUserService.getUserById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AppUserDto createUser(@RequestParam String username) {
    return appUserService.createUser(username);
  }

  @PutMapping("/{id}")
  public AppUserDto updateUser(@PathVariable Long id, @RequestParam String username) {
    return appUserService.updateUsername(id, username);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    appUserService.deleteUser(id);
  }
  @PostMapping("/{userId}/playlists/{playlistId}")
  public AppUserDto addPlaylist(@PathVariable Long userId, @PathVariable Long playlistId) {
    return appUserService.addPlaylistToUser(userId, playlistId);
  }
}