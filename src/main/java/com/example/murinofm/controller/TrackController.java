package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.TrackService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для работы с треками.
 * Предоставляет эндпоинты для получения информации о треках.
 */
@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {
  private final TrackService trackService;

  /**
   * Возвращает трек по его уникальному идентификатору.
   *
   * @param id идентификатор трека (передаётся в пути запроса)
   * @return объект TrackDto, содержащий данные трека
   */
  @GetMapping("/{id}")
  public TrackDto getTrack(@PathVariable Long id) {
    return trackService.getTrackById(id);
  }

  /**
   * Выполняет поиск треков по имени исполнителя.
   *
   * @param artist имя исполнителя (параметр запроса)
   * @return список объектов TrackDto, соответствующих указанному исполнителю
   */
  @GetMapping
  public List<TrackDto> searchTracksByArtist(@RequestParam(required = false) String artist) {
    if (artist != null) {
      return trackService.searchByArtist(artist);
    }
    // Логика, если артист не указан (например, вернуть пустой список)
    return List.of();
  }
}