package com.example.murinofm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AppUser {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String username;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  private List<Playlist> playlists = new ArrayList<>();
}