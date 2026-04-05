package com.example.murinofm.dto;

import com.example.murinofm.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto {
  private Long id;
  private String username;
  private List<PlaylistDto> playlists;

  public static AppUserDto fromEntity(AppUser user) {
    List<PlaylistDto> playlistDtos = user.getPlaylists().stream()
        .map(p -> new PlaylistDto(
            p.getId(),
            p.getName(),
            p.getTracks().stream().map(TrackDto::fromEntity).toList()
        ))
        .toList();
    return new AppUserDto(user.getId(), user.getUsername(), playlistDtos);
  }
}