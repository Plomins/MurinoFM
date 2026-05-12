package com.example.murinofm.dto;

import com.example.murinofm.entity.Track;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO трека")
public class TrackDto {

  @Schema(description = "Идентификатор трека", example = "1")
  private Long id;

  @NotBlank(message = "Название трека обязательно")
  @Schema(description = "Название трека", example = "Battery")
  private String title;

  @Positive(message = "Длительность должна быть положительным числом")
  @Schema(description = "Длительность в секундах", example = "312")
  private Integer durationSeconds;

  @Schema(description = "Имя артиста", example = "Metallica")
  private String artistName;

  @Schema(description = "ID альбома для привязки при создании/обновлении", example = "10", nullable = true)
  private Long albumId;

  @Schema(description = "Информация об альбоме (только для чтения)", nullable = true)
  private AlbumInfo album;

  @Schema(description = "Ссылка на обложку трека")
  private String coverImageUrl;

  @Schema(description = "Ссылка на аудиофайл", example = "/audio/sample1.mp3")
  private String audioUrl;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "Краткая информация об альбоме")
  public static class AlbumInfo {
    private Long id;
    private String title;
  }

  public static TrackDto fromEntity(Track track) {
    AlbumInfo albumInfo = null;
    String artistName = null;
    if (track.getAlbum() != null) {
      albumInfo = new AlbumInfo(
          track.getAlbum().getId(),
          track.getAlbum().getTitle()
      );
      if (track.getAlbum().getArtist() != null) {
        artistName = track.getAlbum().getArtist().getName();
      }
    }

    return new TrackDto(
        track.getId(),
        track.getTitle(),
        track.getDurationSeconds(),
        artistName,
        track.getAlbum() != null ? track.getAlbum().getId() : null,
        albumInfo,
        track.getCoverImageUrl(),   // <-- обложка
        track.getAudioUrl()
    );
  }
}