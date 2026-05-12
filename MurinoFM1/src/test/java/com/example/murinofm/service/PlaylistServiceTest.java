package com.example.murinofm.service;

import com.example.murinofm.dto.PlaylistDto;
import com.example.murinofm.entity.*;
import com.example.murinofm.exception.AppException;
import com.example.murinofm.repository.AppUserRepository;
import com.example.murinofm.repository.PlaylistRepository;
import com.example.murinofm.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

  @Mock private PlaylistRepository playlistRepository;
  @Mock private TrackRepository trackRepository;
  @Mock private AppUserRepository appUserRepository;

  @InjectMocks private PlaylistService playlistService;

  @Test
  void getPlaylistById_Success_Public() {
    AppUser owner = new AppUser(); owner.setId(1L);
    Playlist p = new Playlist(); p.setId(1L); p.setPublic(true); p.setOwner(owner); p.setTracks(new ArrayList<>());

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(p));
    assertNotNull(playlistService.getPlaylistById(1L, null));
  }

  @Test
  void getPlaylistById_Private_AccessDenied() {
    AppUser owner = new AppUser(); owner.setId(10L);
    Playlist p = new Playlist(); p.setPublic(false); p.setOwner(owner);

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(p));
    assertThrows(AppException.class, () -> playlistService.getPlaylistById(1L, 20L));
  }

  @Test
  void getAllPlaylists_Authorized_ShowsAll() {
    AppUser user = new AppUser(); user.setId(1L);
    Playlist p = new Playlist(); p.setPublic(false); p.setOwner(user); p.setTracks(new ArrayList<>());

    when(playlistRepository.findAll()).thenReturn(List.of(p));
    assertEquals(1, playlistService.getAllPlaylists(1L).size());
  }

  @Test
  void getAllPlaylists_Unauthorized_ShowsOnlyPublic() {
    Playlist p1 = new Playlist(); p1.setPublic(true); p1.setTracks(new ArrayList<>());
    AppUser owner = new AppUser(); owner.setId(1L); p1.setOwner(owner);

    when(playlistRepository.findAll()).thenReturn(List.of(p1));
    assertEquals(1, playlistService.getAllPlaylists(null).size());
  }

  @Test
  void createPlaylist_WithTracks_Success() {
    AppUser user = new AppUser(); user.setId(1L);
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(trackRepository.findAllById(any())).thenReturn(new ArrayList<>());
    when(playlistRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    assertNotNull(playlistService.createPlaylist(1L, "List", List.of(1L), true));
  }

  @Test
  void addTrackToPlaylist_Success() {
    AppUser owner = new AppUser(); owner.setId(1L);
    Playlist p = new Playlist(); p.setOwner(owner); p.setTracks(new ArrayList<>());
    Track t = new Track(); t.setId(10L);

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(p));
    when(trackRepository.findById(10L)).thenReturn(Optional.of(t));
    when(playlistRepository.save(any())).thenReturn(p);

    playlistService.addTrackToPlaylist(1L, 10L, 1L);
    assertEquals(1, p.getTracks().size());
  }

  @Test
  void copyPlaylist_FullInitialization_NoNPE() {
    // Владелец оригинала
    AppUser oldOwner = new AppUser(); oldOwner.setId(2L); oldOwner.setUsername("old");

    // Владелец копии
    AppUser newUser = new AppUser(); newUser.setId(1L); newUser.setUsername("new");
    newUser.setPlaylists(new ArrayList<>());

    // Иерархия: Artist -> Album -> Track
    Artist artist = new Artist(); artist.setName("Artist");
    Album album = new Album(); album.setId(100L); album.setArtist(artist);
    Track track = new Track(); track.setId(10L); track.setAlbum(album);

    Playlist original = new Playlist();
    original.setId(5L); original.setName("Original"); original.setOwner(oldOwner);
    original.setTracks(new ArrayList<>(List.of(track)));

    when(playlistRepository.findById(5L)).thenReturn(Optional.of(original));
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(newUser));
    when(playlistRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    PlaylistDto result = playlistService.copyPlaylist(5L, 1L);
    assertNotNull(result);
    assertTrue(result.getName().contains("копия"));
  }

  @Test
  void createMultiplePlaylists_Coverage() {
    playlistService.createMultiplePlaylists(List.of());
  }

  @Test
  void exceptions_Coverage() {
    when(playlistRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> playlistService.getPlaylistById(1L, 1L));
    assertThrows(AppException.class, () -> playlistService.addTrackToPlaylist(1L, 1L, 1L));
  }
  @Test
  void getPlaylistById_OwnerAccess_Success() {
    AppUser owner = new AppUser(); owner.setId(1L);
    Playlist p = new Playlist(); p.setId(1L);
    p.setPublic(false); p.setOwner(owner); p.setTracks(new ArrayList<>());

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(p));

    assertNotNull(playlistService.getPlaylistById(1L, 1L));
  }

  @Test
  void createPlaylist_WithNoTrackIds_Coverage() {
    AppUser user = new AppUser(); user.setId(1L);
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(playlistRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    assertNotNull(playlistService.createPlaylist(1L, "EmptyList", null, true));
    verify(trackRepository, never()).findAllById(any());
  }

  @Test
  void addTrackToPlaylist_AlreadyContainsTrack_Coverage() {
    AppUser owner = new AppUser(); owner.setId(1L);
    Track t = new Track(); t.setId(10L);

    Playlist p = new Playlist();
    p.setOwner(owner);
    p.setTracks(new ArrayList<>(List.of(t)));

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(p));
    when(trackRepository.findById(10L)).thenReturn(Optional.of(t));
    when(playlistRepository.save(any())).thenReturn(p);

    playlistService.addTrackToPlaylist(1L, 10L, 1L);

    assertEquals(1, p.getTracks().size());
  }

  @Test
  void exceptions_PlaylistOwnerNotFound_In_AddTrack() {
    Playlist p = new Playlist();
    AppUser owner = new AppUser(); owner.setId(1L);
    p.setOwner(owner);

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(p));

    assertThrows(AppException.class, () -> playlistService.addTrackToPlaylist(1L, 10L, 999L));
    when(trackRepository.findById(10L)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> playlistService.addTrackToPlaylist(1L, 10L, 1L));
  }

  @Test
  void copyPlaylist_OriginNotFound_ThrowsException() {

    when(playlistRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> playlistService.copyPlaylist(1L, 1L));
  }


  @Test
  void copyPlaylist_WithNoTracks_Coverage() {
    AppUser user = new AppUser();
    user.setId(1L);
    user.setUsername("testUser");
    user.setPlaylists(new ArrayList<>());

    Playlist original = new Playlist();
    original.setId(5L);
    original.setName("Empty Origin");
    original.setOwner(user);
    original.setTracks(new ArrayList<>());

    when(playlistRepository.findById(5L)).thenReturn(Optional.of(original));
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));

    when(playlistRepository.save(any(Playlist.class))).thenAnswer(i -> i.getArgument(0));

    PlaylistDto result = playlistService.copyPlaylist(5L, 1L);

    assertNotNull(result);
    assertTrue(result.getName().contains("копия"));
    verify(playlistRepository).save(any(Playlist.class));
  }

  @Test
  void createPlaylist_WithEmptyTrackIds_Coverage() {
    AppUser user = new AppUser(); user.setId(1L);
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(playlistRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    PlaylistDto result = playlistService.createPlaylist(1L, "Empty", new ArrayList<>(), true);
    assertNotNull(result);
  }
  @Test
  void getPlaylistById_UnauthorizedAndPublic_Success() {
    Playlist playlist = new Playlist();
    playlist.setPublic(true);
    AppUser owner = new AppUser(); owner.setId(99L);
    playlist.setOwner(owner);
    playlist.setTracks(new ArrayList<>());

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

    PlaylistDto result = playlistService.getPlaylistById(1L, null);
    assertNotNull(result);
  }


  @Test
  void addTrackToPlaylist_TrackAlreadyInPlaylist_Coverage() {
    AppUser owner = new AppUser(); owner.setId(1L);
    Track track = new Track(); track.setId(10L);
    Playlist playlist = new Playlist();
    playlist.setOwner(owner);
    playlist.setTracks(new ArrayList<>(List.of(track)));
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    when(trackRepository.findById(10L)).thenReturn(Optional.of(track));
    when(playlistRepository.save(any())).thenReturn(playlist);

    playlistService.addTrackToPlaylist(1L, 10L, 1L);
    assertEquals(1, playlist.getTracks().size());
  }
  @Test
  void copyPlaylist_WithEmptyTracks_Coverage() {
    AppUser user = new AppUser();
    user.setId(1L);
    user.setUsername("testUser");
    user.setPlaylists(new ArrayList<>());


    Playlist original = new Playlist();
    original.setId(5L);
    original.setName("Empty Origin");
    original.setOwner(user);
    original.setTracks(new ArrayList<>());

    when(playlistRepository.findById(5L)).thenReturn(Optional.of(original));
    when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
    when(playlistRepository.save(any(Playlist.class))).thenAnswer(i -> i.getArgument(0));


    PlaylistDto result = playlistService.copyPlaylist(5L, 1L);

    assertNotNull(result);
    assertTrue(result.getName().contains("копия"));
    assertTrue(result.getTrackIds() == null || result.getTrackIds().isEmpty());
    verify(playlistRepository).save(any(Playlist.class));
  }
  @Test
  void getPlaylistById_Private_NullUserId_Denied() {
    AppUser owner = new AppUser(); owner.setId(5L);
    Playlist p = new Playlist(); p.setPublic(false); p.setOwner(owner);

    when(playlistRepository.findById(1L)).thenReturn(Optional.of(p));
    assertThrows(AppException.class, () -> playlistService.getPlaylistById(1L, null));
  }

  @Test
  void getAllPlaylists_Authorized_FiltersOutOtherPrivate() {
    AppUser otherOwner = new AppUser(); otherOwner.setId(99L);
    Playlist privateOther = new Playlist();
    privateOther.setPublic(false); privateOther.setOwner(otherOwner);
    privateOther.setTracks(new ArrayList<>());

    when(playlistRepository.findAll()).thenReturn(List.of(privateOther));
    assertEquals(0, playlistService.getAllPlaylists(1L).size());
  }

  @Test
  void createPlaylist_UserNotFound_ThrowsException() {
    when(appUserRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(AppException.class,
        () -> playlistService.createPlaylist(99L, "Test", null, true));
  }

  @Test
  void copyPlaylist_UserNotFound_ThrowsException() {
    Playlist original = new Playlist(); original.setId(5L);
    original.setName("Original"); original.setTracks(new ArrayList<>());
    AppUser owner = new AppUser(); owner.setId(2L);
    original.setOwner(owner);

    when(playlistRepository.findById(5L)).thenReturn(Optional.of(original));
    when(appUserRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> playlistService.copyPlaylist(5L, 99L));
  }
  @Test
  void getAllPlaylists_Authorized_IncludesPublicFromOthers() {
    AppUser otherOwner = new AppUser(); otherOwner.setId(99L);
    Playlist publicPlaylist = new Playlist();
    publicPlaylist.setPublic(true);
    publicPlaylist.setOwner(otherOwner);
    publicPlaylist.setTracks(new ArrayList<>());

    when(playlistRepository.findAll()).thenReturn(List.of(publicPlaylist));

    assertEquals(1, playlistService.getAllPlaylists(1L).size());
  }
}
