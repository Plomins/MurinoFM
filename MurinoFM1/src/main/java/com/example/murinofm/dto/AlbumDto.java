package com.example.murinofm.dto;

import com.example.murinofm.entity.Album;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO альбома")
public class AlbumDto {
  @Schema(description = "ID альбома", example = "23")
  private Long id;

  @Schema(description = "Название альбома", example = "europaFM")
  private String title;

  @Schema(description = "Артист")
  private ArtistRefDto artist;

  @Schema(description = "Список треков (без вложенной информации об альбоме)")
  private List<TrackDto> tracks;

  public static AlbumDto fromEntity(Album album) {
    return new AlbumDto(
        album.getId(),
        album.getTitle(),
        new ArtistRefDto(album.getArtist().getId(), album.getArtist().getName()),
        album.getTracks().stream()
            .map(TrackDto::fromEntity)
            .collect(Collectors.toList())
    );
  }
}