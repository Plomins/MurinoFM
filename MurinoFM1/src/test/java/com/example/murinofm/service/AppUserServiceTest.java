package com.example.murinofm.service;

import com.example.murinofm.dto.AppUserDto;
import com.example.murinofm.entity.AppUser;
import com.example.murinofm.entity.Playlist;
import com.example.murinofm.exception.AppException;
import com.example.murinofm.exception.ConflictException;
import com.example.murinofm.repository.AppUserRepository;
import com.example.murinofm.repository.PlaylistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

  @Mock
  private AppUserRepository appUserRepository;

  @Mock
  private PlaylistRepository playlistRepository;

  @InjectMocks
  private AppUserService appUserService;

  @Test
  void createUser_Success() {
    String username = "testUser";
    AppUser user = new AppUser();
    user.setId(1L);
    user.setUsername(username);
    when(appUserRepository.existsByUsername(username)).thenReturn(false);
    when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

    AppUserDto result = appUserService.createUser(username);

    assertNotNull(result);
    verify(appUserRepository).save(any(AppUser.class));
  }

  @Test
  void createUser_ThrowsConflictException() {
    String username = "testUser";
    when(appUserRepository.existsByUsername(username)).thenReturn(true);

    assertThrows(ConflictException.class, () -> appUserService.createUser(username));
    verify(appUserRepository, never()).save(any());
  }

  @Test
  void addPlaylistToUser_Success() {
    AppUser user = new AppUser();
    user.setId(1L);
    user.setPlaylists(new ArrayList<>());

    Playlist playlist = new Playlist();
    playlist.setId(1L);
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

    appUserService.addPlaylistToUser(1L, 1L);

    assertEquals(user, playlist.getOwner());
    assertTrue(user.getPlaylists().contains(playlist));
    verify(appUserRepository).save(user);
  }
  @Test
  void getAllUsers_Success() {
    AppUser user = new AppUser();
    user.setId(1L);
    user.setUsername("user1");

    when(appUserRepository.findAll()).thenReturn(List.of(user));

    List<AppUserDto> result = appUserService.getAllUsers();
    assertFalse(result.isEmpty());
    assertEquals("user1", result.get(0).getUsername());
  }

  @Test
  void getUserById_Success() {
    AppUser user = new AppUser();
    user.setId(1L);
    user.setUsername("user1");

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));

    AppUserDto result = appUserService.getUserById(1L);

    assertEquals("user1", result.getUsername());
  }

  @Test
  void getUserById_NotFound_ThrowsException() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> appUserService.getUserById(1L));
  }

  @Test
  void updateUsername_Success() {
    AppUser user = new AppUser();
    user.setId(1L);
    user.setUsername("oldName");

    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

    AppUserDto result = appUserService.updateUsername(1L, "newName");

    assertEquals("newName", user.getUsername());
    verify(appUserRepository).save(user);
  }

  @Test
  void deleteUser_Success() {
    when(appUserRepository.existsById(1L)).thenReturn(true);

    appUserService.deleteUser(1L);

    verify(appUserRepository).deleteById(1L);
  }

  @Test
  void deleteUser_NotFound_ThrowsException() {
    when(appUserRepository.existsById(1L)).thenReturn(false);
    assertThrows(AppException.class, () -> appUserService.deleteUser(1L));
  }
  @Test
  void addPlaylistToUser_PlaylistNotFound_ThrowsException() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(new AppUser()));
    when(playlistRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> appUserService.addPlaylistToUser(1L, 2L));
  }
  @Test
  void updateUsername_NotFound_ThrowsException() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> appUserService.updateUsername(1L, "newName"));
  }

  @Test
  void addPlaylistToUser_UserNotFound_ThrowsException() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> appUserService.addPlaylistToUser(1L, 10L));
    verify(playlistRepository, never()).findById(anyLong());
}
}