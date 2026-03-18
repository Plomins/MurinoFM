package com.example.murinofm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artists")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Artist {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Album> albums = new ArrayList<>();
}