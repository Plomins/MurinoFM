package com.example.murinofm.repository;

import com.example.murinofm.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByArtistContainingIgnoreCase(String artist);
}