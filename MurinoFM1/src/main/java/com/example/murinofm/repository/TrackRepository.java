package com.example.murinofm.repository;

import com.example.murinofm.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

  List<Track> findByTitleContainingIgnoreCase(String title);

  @Override
  @EntityGraph(attributePaths = {"album", "album.artist"})
  List<Track> findAll();

  @Query("SELECT t FROM Track t JOIN t.album a JOIN a.artist ar WHERE LOWER(ar.name) = LOWER(:artistName)")
  Page<Track> findByArtistName(@Param("artistName") String artistName, Pageable pageable);

  @Query(value = "SELECT t.* FROM tracks t " +
      "JOIN albums a ON t.album_id = a.id " +
      "JOIN artists ar ON a.artist_id = ar.id " +
      "WHERE LOWER(ar.name) = LOWER(:artistName)",

      nativeQuery = true)
  Page<Track> findByArtistNameNative(@Param("artistName") String artistName, Pageable pageable);
}