package com.example.murinofm.dto;

import lombok.Data;
import java.util.List;

@Data
public class MultiPlaylistRequest {
  private String name1;
  private List<Long> trackIds1;

  private String name2;
  private List<Long> trackIds2;
}