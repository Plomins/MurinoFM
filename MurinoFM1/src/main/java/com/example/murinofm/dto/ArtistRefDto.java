package com.example.murinofm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Краткая информация об артисте")
public class ArtistRefDto {
  @Schema(description = "ID артиста", example = "25")
  private Long id;
  @Schema(description = "Имя артиста", example = "turborosho")
  private String name;
}