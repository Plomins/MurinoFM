package com.example.murinofm.repository;
import com.example.murinofm.entity.Artist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

  @Override
  @EntityGraph(attributePaths = {"albums"})
  List<Artist> findAll();
}