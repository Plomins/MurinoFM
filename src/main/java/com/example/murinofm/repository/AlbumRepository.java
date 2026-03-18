package com.example.murinofm.repository;
import com.example.murinofm.entity.Album;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
  @EntityGraph(attributePaths = "tracks")
  @Query("SELECT a FROM Album a")
  List<Album> findAllWithTracks();
}