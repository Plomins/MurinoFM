package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.AlbumService;
import com.example.murinofm.service.ArtistService;
import com.example.murinofm.service.TrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
@Tag(name = "Треки и артисты", description = "Управление треками, артистами, альбомами и демонстрационные запросы")
public class TrackController {

  private final TrackService trackService;
  private final ArtistService artistService;
  private final AlbumService albumService;   // теперь final, внедряется Lombok'ом

  @Operation(summary = "Получить список всех альбомов")
  @GetMapping("/albums")
  public List<String> getAllAlbums() {
    return artistService.getAllAlbumsList();
  }

  @Operation(summary = "Получить список всех артистов")
  @GetMapping("/artists")
  public List<String> showAllArtists() {
    return artistService.getAllArtistsList();
  }

  @Operation(summary = "Получить все треки", description = "Возвращает список треков. Можно отфильтровать по названию трека (опциональный параметр title)")
  @GetMapping("/tracks")
  public ResponseEntity<Map<String, Object>> getTracks(
      @RequestParam(required = false) String title,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    if (title != null && !title.isBlank()) {
      List<TrackDto> list = trackService.searchByTitle(title);
      Map<String, Object> result = new HashMap<>();
      result.put("content", list);
      result.put("totalPages", 1);
      result.put("totalElements", list.size());
      return ResponseEntity.ok(result);
    }

    Page<TrackDto> trackPage = trackService.getAllTracks(PageRequest.of(page, size));
    Map<String, Object> result = new HashMap<>();
    result.put("content", trackPage.getContent());
    result.put("totalPages", trackPage.getTotalPages());
    result.put("totalElements", trackPage.getTotalElements());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "Получить трек по ID")
  @GetMapping("/tracks/{id}")
  public TrackDto getTrackById(
      @Parameter(description = "ID трека") @PathVariable Long id) {
    return trackService.getTrackById(id);
  }

  @Operation(summary = "Поиск треков по названию (альтернативный эндпоинт)")
  @GetMapping("/tracks/search")
  public List<TrackDto> searchTracks(
      @Parameter(description = "Точное или частичное название трека") @RequestParam("name") String name) {
    return trackService.searchByTitle(name);
  }

  @Operation(summary = "Создать трек", description = "Создаёт трек. Если передан albumId, трек будет привязан к альбому.")
  @PostMapping("/tracks")
  @ResponseStatus(HttpStatus.CREATED)
  public TrackDto createTrack(@Valid @RequestBody TrackDto trackDto) {
    return trackService.save(trackDto);
  }

  @Operation(summary = "Удалить трек по ID")
  @DeleteMapping("/tracks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @Parameter(description = "ID трека") @PathVariable Long id) {
    trackService.delete(id);
  }

  @Operation(summary = "Создать артиста")
  @PostMapping("/artists")
  @ResponseStatus(HttpStatus.CREATED)
  public String createArtist(
      @Parameter(description = "Имя артиста") @RequestParam String name) {
    return artistService.saveArtist(name);
  }

  @Operation(summary = "Удалить артиста и все его альбомы")
  @DeleteMapping("/artists/{id}")
  public String deleteArtist(
      @Parameter(description = "ID артиста") @PathVariable Long id) {
    return artistService.deleteArtist(id);
  }

  @Operation(summary = "Добавить альбом артисту")
  @PostMapping("/artists/{id}/albums")
  @ResponseStatus(HttpStatus.CREATED)
  public String addAlbum(@PathVariable Long id, @RequestParam String title) {
    artistService.addAlbumToArtist(id, title);
    String safeTitle = HtmlUtils.htmlEscape(title);
    return "Альбом '" + safeTitle + "' успешно добавлен.";
  }

  @Operation(summary = "Удалить альбом по ID")
  @DeleteMapping("/albums/{id}")
  public String deleteAlbum(
      @Parameter(description = "ID альбома") @PathVariable Long id) {
    return artistService.deleteAlbum(id);
  }

  @Operation(summary = "Фильтрация треков по имени артиста (JPQL)", description = "Постраничный поиск треков по имени артиста с кешированием")
  @GetMapping("/tracks/by-artist")
  public Page<TrackDto> getTracksByArtist(
      @Parameter(description = "Имя артиста") @RequestParam String artistName,
      @Parameter(description = "Номер страницы (начинается с 0)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
    return trackService.findByArtistName(artistName, PageRequest.of(page, size));
  }

  @Operation(summary = "Фильтрация треков по имени артиста (native SQL)", description = "Аналог предыдущего, но через нативный SQL")
  @GetMapping("/tracks/by-artist-native")
  public Page<TrackDto> getTracksByArtistNative(
      @Parameter(description = "Имя артиста") @RequestParam String artistName,
      @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
    return trackService.findByArtistNameNative(artistName, PageRequest.of(page, size));
  }

  @Operation(summary = "Массовое создание треков", description = "Создаёт список треков. Если какой-либо albumId не найден, операция полностью откатывается.")
  @PostMapping("/tracks/bulk")
  @ResponseStatus(HttpStatus.CREATED)
  public List<TrackDto> bulkCreateTracks(@Valid @RequestBody List<TrackDto> trackDtos) {
    return trackService.bulkCreate(trackDtos);
  }
}