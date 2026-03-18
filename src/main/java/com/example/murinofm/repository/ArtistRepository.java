package com.example.murinofm.repository;
import com.example.murinofm.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ArtistRepository extends JpaRepository<Artist, Long> {

}