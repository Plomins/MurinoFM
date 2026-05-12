package com.example.murinofm.dto;

import com.example.murinofm.entity.Playlist;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Данные плейлиста")
public class PlaylistDto {

  @Schema(description = "Идентификатор плейлиста")
  private Long id;
  private String name;
  private List<TrackDto> tracks;

  // для входящих запросов (создание)
  private List<Long> trackIds;

  private boolean isPublic = true;

  public PlaylistDto(Long id, String name, List<TrackDto> tracks) {
    this.id = id;
    this.name = name;
    this.tracks = tracks;
  }

  public static PlaylistDto fromEntity(Playlist playlist) {
    List<TrackDto> trackDtos = playlist.getTracks().stream()
        .map(TrackDto::fromEntity)
        .toList();
    PlaylistDto dto = new PlaylistDto(playlist.getId(), playlist.getName(), trackDtos);
    dto.setPublic(playlist.isPublic());
    return dto;
  }
}