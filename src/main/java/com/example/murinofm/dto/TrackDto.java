package com.example.murinofm.dto;

import com.example.murinofm.entity.Track;

public record TrackDto(
        Long id,
        String title,
        String artist,
        Integer durationSeconds
) {
    public static TrackDto fromEntity(Track track) {
        return new TrackDto(
                track.getId(),
                track.getTitle(),
                track.getArtist(),
                track.getDurationSeconds()
        );
    }
}