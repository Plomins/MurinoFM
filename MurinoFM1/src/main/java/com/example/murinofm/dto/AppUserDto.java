package com.example.murinofm.dto;

import com.example.murinofm.entity.AppUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные пользователя")
public class AppUserDto {

  @Schema(description = "Идентификатор пользователя", example = "1")
  private Long id;

  @Schema(description = "Имя пользователя", example = "rock_fan")
  private String username;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Schema(description = "Список плейлистов пользователя")
  private List<PlaylistDto> playlists;

  @Schema(description = "Ссылка на аватар пользователя")
  private String avatarUrl;


  public AppUserDto(Long id, String username, String password, List<PlaylistDto> playlists) {
    this(id, username, password, playlists, null);
  }

  public static AppUserDto fromEntity(AppUser user) {
    List<PlaylistDto> playlistDtos = user.getPlaylists().stream()
        .map(p -> new PlaylistDto(
            p.getId(),
            p.getName(),
            p.getTracks().stream().map(TrackDto::fromEntity).toList()
        ))
        .toList();
    return new AppUserDto(
        user.getId(),
        user.getUsername(),
        null,                         // пароль не передаём
        playlistDtos,
        user.getAvatarUrl()           // аватарка
    );
  }
}