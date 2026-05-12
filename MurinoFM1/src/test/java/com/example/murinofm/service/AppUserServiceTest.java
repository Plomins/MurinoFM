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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

  @Mock private AppUserRepository appUserRepository;
  @Mock private PlaylistRepository playlistRepository;
  @InjectMocks private AppUserService appUserService;

  @Test
  void getAllUsers_Success() {
    when(appUserRepository.findAll()).thenReturn(List.of(new AppUser()));
    List<AppUserDto> result = appUserService.getAllUsers();
    assertEquals(1, result.size());
  }

  @Test
  void getUserById_Found() {
    AppUser user = new AppUser();
    user.setId(1L);
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    assertNotNull(appUserService.getUserById(1L));
  }

  @Test
  void getUserById_NotFound_ThrowsException() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> appUserService.getUserById(1L));
  }

  @Test
  void createUser_Success() {
    when(appUserRepository.existsByUsername("admin")).thenReturn(false);
    when(appUserRepository.save(any())).thenReturn(new AppUser());
    assertNotNull(appUserService.createUser("admin", "pass"));
  }

  @Test
  void createUser_AlreadyExists_ThrowsConflict() {
    when(appUserRepository.existsByUsername("admin")).thenReturn(true);
    assertThrows(ConflictException.class, () -> appUserService.createUser("admin", "pass"));
  }

  @Test
  void authenticate_Success() {
    AppUser user = new AppUser();
    user.setUsername("user");
    user.setPassword("123");
    when(appUserRepository.findAll()).thenReturn(List.of(user));
    assertNotNull(appUserService.authenticate("user", "123"));
  }

  @Test
  void authenticate_WrongCredentials_ThrowsException() {
    when(appUserRepository.findAll()).thenReturn(List.of());
    assertThrows(AppException.class, () -> appUserService.authenticate("u", "p"));
  }

  @Test
  void deleteUser_Success() {
    when(appUserRepository.existsById(1L)).thenReturn(true);
    appUserService.deleteUser(1L);
    verify(appUserRepository).deleteById(1L);
  }

  @Test
  void addPlaylistToUser_Success() {
    AppUser user = new AppUser();
    user.setPlaylists(new ArrayList<>());
    Playlist playlist = new Playlist();
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(playlistRepository.findById(2L)).thenReturn(Optional.of(playlist));
    when(appUserRepository.save(any())).thenReturn(user);

    appUserService.addPlaylistToUser(1L, 2L);
    assertEquals(user, playlist.getOwner());
  }
  @Test
  void updateUsername_UserNotFound_ThrowsException() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> appUserService.updateUsername(1L, "new"));
  }

  @Test
  void updateAvatar_Success() {
    AppUser user = new AppUser();
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(appUserRepository.save(any())).thenReturn(user);

    appUserService.updateAvatar(1L, "http://avatar.url");
    assertEquals("http://avatar.url", user.getAvatarUrl());
  }

  @Test
  void searchUsers_Success() {
    when(appUserRepository.findByUsernameContainingIgnoreCase("test")).thenReturn(List.of(new AppUser()));
    List<AppUserDto> result = appUserService.searchUsers("test");
    assertEquals(1, result.size());
  }

  @Test
  void addPlaylistToUser_PlaylistNotFound_ThrowsException() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(new AppUser()));
    when(playlistRepository.findById(2L)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> appUserService.addPlaylistToUser(1L, 2L));
  }
  @Test
  void updateUsername_Success() {
    AppUser user = new AppUser(); user.setId(1L);
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(appUserRepository.save(any())).thenReturn(user);

    appUserService.updateUsername(1L, "newUsername");
    assertEquals("newUsername", user.getUsername());
  }

  @Test
  void addPlaylistToUser_UserNotFound_ThrowsException() {
    when(appUserRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> appUserService.addPlaylistToUser(999L, 1L));
  }
  @Test
  void authenticate_UserNotFound_ThrowsException() {
    when(appUserRepository.findAll()).thenReturn(List.of());
    assertThrows(AppException.class, () -> appUserService.authenticate("none", "none"));
  }

  @Test
  void searchUsers_EmptyResult_Success() {
    when(appUserRepository.findByUsernameContainingIgnoreCase("empty")).thenReturn(List.of());
    List<AppUserDto> result = appUserService.searchUsers("empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void authenticate_WrongPassword_ThrowsException() {
    AppUser user = new AppUser();
    user.setUsername("admin");
    user.setPassword("pass");

    when(appUserRepository.findAll()).thenReturn(List.of(user));

    assertThrows(AppException.class, () -> appUserService.authenticate("admin", "wrong_pass"));
  }

  @Test
  void updateAvatar_UserNotFound_ThrowsException() {
    when(appUserRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> appUserService.updateAvatar(1L, "url"));
  }
  @Test
  void deleteUser_NotFound_ThrowsException() {
    when(appUserRepository.existsById(99L)).thenReturn(false);
    assertThrows(AppException.class, () -> appUserService.deleteUser(99L));
  }
  @Test
  void updateUsername_NotFound_ThrowsException() {
    when(appUserRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> appUserService.updateUsername(1L, "new"));
  }
  @Test
  void authenticate_ComprehensiveBranchCoverage() {
    AppUser user1 = new AppUser(); user1.setUsername("wrong"); user1.setPassword("wrong");
    AppUser user2 = new AppUser(); user2.setUsername("admin"); user2.setPassword("wrong");
    AppUser user3 = new AppUser(); user3.setUsername("admin"); user3.setPassword("admin");
    user3.setId(1L);

    when(appUserRepository.findAll()).thenReturn(List.of(user1, user2, user3));

    AppUserDto result = appUserService.authenticate("admin", "admin");
    assertEquals(1L, result.getId());
  }
  @Test
  void authenticate_BranchCoverage() {
    AppUser u1 = new AppUser(); u1.setUsername("user1"); u1.setPassword("pass1");
    AppUser u2 = new AppUser(); u2.setUsername("target"); u2.setPassword("wrong");
    AppUser u3 = new AppUser(); u3.setUsername("target"); u3.setPassword("correct");
    u3.setId(3L);

    when(appUserRepository.findAll()).thenReturn(List.of(u1, u2, u3));

    AppUserDto result = appUserService.authenticate("target", "correct");
    assertEquals(3L, result.getId());
  }

  @Test
  void deleteUser_NotFound_Exception() {
    when(appUserRepository.existsById(1L)).thenReturn(false);
    assertThrows(AppException.class, () -> appUserService.deleteUser(1L));
  }

}
