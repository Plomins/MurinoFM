package com.example.murinofm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albums")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Album {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artist_id", nullable = false)

  private Artist artist;

  @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Track> tracks = new ArrayList<>();
}