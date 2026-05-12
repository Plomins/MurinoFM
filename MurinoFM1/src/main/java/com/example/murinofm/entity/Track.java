package com.example.murinofm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tracks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Track {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private Integer durationSeconds;
  private String coverImageUrl;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "album_id")
  private Album album;

  @ManyToMany(mappedBy = "tracks", fetch = FetchType.LAZY)
  private List<Playlist> playlists = new ArrayList<>();

  private String audioUrl;
}