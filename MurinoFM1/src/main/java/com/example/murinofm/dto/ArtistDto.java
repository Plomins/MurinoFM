package com.example.murinofm.dto;

import com.example.murinofm.entity.Artist;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO артиста с альбомами")
public class ArtistDto {

  @Schema(description = "ID артиста", example = "25")
  private Long id;

  @Schema(description = "Имя артиста", example = "turborosho")
  private String name;

  @Schema(description = "Альбомы артиста")
  private List<AlbumDto> albums;

  public static ArtistDto fromEntity(Artist artist) {
    return new ArtistDto(
        artist.getId(),
        artist.getName(),
        artist.getAlbums().stream()
            .map(AlbumDto::fromEntity)
            .collect(Collectors.toList())
    );
  }
}