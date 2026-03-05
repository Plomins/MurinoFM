package com.example.murinofm.repository;

import com.example.murinofm.entity.Track;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

  List<Track> findByArtistContainingIgnoreCase(String artist);
}