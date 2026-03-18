package com.example.murinofm.dto;

import com.example.murinofm.entity.Track;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackDto {
  private Long id;
  private String title;
  private Integer durationSeconds;

  public static TrackDto fromEntity(Track track) {
    return new TrackDto(
        track.getId(),
        track.getTitle(),
        track.getDurationSeconds()
    );
  }
}
/*./psql -U postgres -d murinofm_db*/