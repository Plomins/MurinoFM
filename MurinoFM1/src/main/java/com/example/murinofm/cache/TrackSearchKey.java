package com.example.murinofm.cache;

import java.util.Objects;

public class TrackSearchKey {
  private final String artistName;
  private final int page;
  private final int size;

  public TrackSearchKey(String artistName, int page, int size) {
    this.artistName = artistName;
    this.page = page;
    this.size = size;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;

    }
    if (!(o instanceof TrackSearchKey that)) {
      return false;
    }
    return page == that.page &&
        size == that.size &&
        Objects.equals(artistName, that.artistName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artistName, page, size);
  }
}