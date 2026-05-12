package com.example.murinofm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Playlist {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private AppUser owner;

  @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "playlist_tracks",
      joinColumns = @JoinColumn(name = "playlist_id"),
      inverseJoinColumns = @JoinColumn(name = "track_id")
  )
  private List<Track> tracks = new ArrayList<>();

  private boolean isPublic = true;
}