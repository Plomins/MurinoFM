package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.TrackService;
import com.example.murinofm.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TrackController {

  private final TrackService trackService;
  private final ArtistService artistService;

  @GetMapping("/tracks")
  public List<TrackDto> getTracks(@RequestParam(required = false) String title) {
    return (title != null && !title.isBlank()) ? trackService.searchByTitle(title) : trackService.getAllTracks();
  }

  @DeleteMapping("/tracks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    trackService.delete(id);
  }

  @PostMapping("/artists")
  @ResponseStatus(HttpStatus.CREATED)
  public String createArtist(@RequestParam String name) {
    return artistService.saveArtist(name);
  }


  @PostMapping("/artists/{id}/albums")
  @ResponseStatus(HttpStatus.CREATED)
  public String addAlbum(@PathVariable Long id, @RequestParam String title) {
    return artistService.addAlbumToArtist(id, title);
  }

  @DeleteMapping("/artists/{id}")
  public String deleteArtist(@PathVariable Long id) {
    return artistService.deleteArtist(id);
  }

  @DeleteMapping("/albums/{id}")
  public String deleteAlbum(@PathVariable Long id) {
    return artistService.deleteAlbum(id);
  }
  @GetMapping("/test/n-plus-1")
  public String getStepN1() {
    return artistService.demonstrateNPlus1();
  }
}