package com.example.murinofm.controller;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @GetMapping("/{id}")
    public TrackDto getTrack(@PathVariable Long id) {
        return trackService.getTrackById(id);
    }

    @GetMapping("/search")
    public List<TrackDto> searchTracksByArtist(@RequestParam String artist) {
        return trackService.searchByArtist(artist);
    }
}