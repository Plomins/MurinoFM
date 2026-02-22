package com.example.murinofm.service;

import com.example.murinofm.dto.TrackDto;
import com.example.murinofm.entity.Track;
import com.example.murinofm.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;

    public TrackDto getTrackById(Long id) {
        Track track = trackRepository.findById(id).orElseThrow();
        return new TrackDto(track.getId(), track.getTitle(), track.getArtist(), track.getDurationSeconds());
    }

    public List<TrackDto> searchByArtist(String artist) {
        return trackRepository.findByArtistContainingIgnoreCase(artist)
                .stream()
                .map(track -> new TrackDto(track.getId(), track.getTitle(), track.getArtist(), track.getDurationSeconds()))
                // Если в TrackDto есть метод fromEntity, можно использовать его: .map(TrackDto::fromEntity)
                .collect(Collectors.toList());
    }
}