package com.example.murinofm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PlaylistDto {
  private Long id;
  private String name;
  private List<TrackDto> tracks;
}