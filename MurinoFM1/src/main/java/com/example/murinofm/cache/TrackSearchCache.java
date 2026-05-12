package com.example.murinofm.cache;

import com.example.murinofm.dto.TrackDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrackSearchCache {
  private final Map<TrackSearchKey, Page<TrackDto>> cache = new HashMap<>();

  public Page<TrackDto> get(TrackSearchKey key) {
    return cache.get(key);
  }

  public void put(TrackSearchKey key, Page<TrackDto> page) {
    cache.put(key, page);
  }

  public void invalidate() {
    cache.clear();
  }
}