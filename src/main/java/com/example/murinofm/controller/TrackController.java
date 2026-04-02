package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.AlbumService;
import com.example.murinofm.service.TrackService;
import com.example.murinofm.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TrackController {

  private final TrackService trackService;
  private final ArtistService artistService;

  @GetMapping("/albums")
  public List<String> getAllAlbums() {
    return artistService.getAllAlbumsList();
  }
  @GetMapping("/artists")
  public List<String> showAllArtists() {
    return artistService.getAllArtistsList();
  }
  @GetMapping("/tracks")
  public List<TrackDto> getTracks(@RequestParam(required = false) String title) {
    return (title != null && !title.isBlank())
        ? trackService.searchByTitle(title)
        : trackService.getAllTracks();
  }

  @GetMapping("/tracks/search")
  public List<TrackDto> searchTracks(@RequestParam("name") String name) {
    return trackService.searchByTitle(name);
  }

  @PostMapping("/tracks")
  @ResponseStatus(HttpStatus.CREATED)
  public TrackDto createTrack(@RequestBody TrackDto trackDto) {
    return trackService.save(trackDto);
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
  @Autowired
  private AlbumService albumService;
  @GetMapping("/demo/no-transaction")
  public String demoNoTransaction(
      @RequestParam(defaultValue = "false") boolean error) {
    albumService.saveDataWithoutTransaction(error);
    return "Сохранено без транзакции";
  }

  @GetMapping("/demo/with-transaction")
  public String demoWithTransaction(
      @RequestParam(defaultValue = "false") boolean error) {
    albumService.saveDataWithTransaction(error);
    return "Сохранено с транзакцией";
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