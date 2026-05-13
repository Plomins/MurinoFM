package com.example.murinofm.controller;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FileController {

  private static final Logger log = LoggerFactory.getLogger(FileController.class);

  @Value("${app.audio.upload.dir:/app/uploads/audio}")
  private String AUDIO_DIR;

  @Value("${app.image.upload.dir:/app/uploads/images}")
  private String IMAGE_DIR;

  // ------------------- АУДИО -------------------
  @PostMapping("/upload/audio")
  public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
    try {
      if (file.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Файл пуст"));
      }
      String originalFilename = file.getOriginalFilename();
      String extension = "";
      if (originalFilename != null && originalFilename.contains(".")) {
        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
      }
      String newFilename = UUID.randomUUID().toString() + extension;
      Path dir = Paths.get(AUDIO_DIR);
      if (!Files.exists(dir)) Files.createDirectories(dir);
      Path filePath = dir.resolve(newFilename);
      file.transferTo(filePath.toFile());

      String audioUrl = "/api/audio/" + newFilename;
      log.info("Аудио сохранено: {}", filePath);
      return ResponseEntity.ok(Map.of("audioUrl", audioUrl, "filename", originalFilename));
    } catch (IOException e) {
      log.error("Ошибка загрузки аудио", e);
      return ResponseEntity.internalServerError().body(Map.of("error", "Не удалось сохранить аудио"));
    }
  }

  @GetMapping("/audio/{filename}")
  public ResponseEntity<Resource> getAudio(@PathVariable String filename,
                                           @RequestHeader(value = "Range", required = false) String rangeHeader) {
    try {
      Path filePath = Paths.get(AUDIO_DIR).resolve(filename).normalize();
      if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
        return ResponseEntity.notFound().build();
      }
      long fileSize = Files.size(filePath);
      Resource resource = new UrlResource(filePath.toUri());
      if (rangeHeader == null) {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("audio/mpeg"))
            .contentLength(fileSize)
            .header(HttpHeaders.ACCEPT_RANGES, "bytes")
            .body(resource);
      }
      // Range‑запрос (без изменений)
      String rangeValue = rangeHeader.replace("bytes=", "").trim();
      String[] ranges = rangeValue.split("-");
      long start = Long.parseLong(ranges[0]);
      long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;
      if (end >= fileSize) end = fileSize - 1;
      long contentLength = end - start + 1;
      if (start >= fileSize || end < start) {
        return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
            .build();
      }
      RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r");
      raf.seek(start);
      byte[] data = new byte[(int) contentLength];
      raf.readFully(data);
      raf.close();

      InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(data));
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
      headers.setContentLength(contentLength);
      headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);
      headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
      return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.PARTIAL_CONTENT);
    } catch (IOException e) {
      log.error("Ошибка при чтении аудио", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  // ------------------- ИЗОБРАЖЕНИЯ -------------------
  @PostMapping("/upload/image")
  public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
    try {
      if (file.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Файл пуст"));
      }
      String originalFilename = file.getOriginalFilename();
      String extension = "";
      if (originalFilename != null && originalFilename.contains(".")) {
        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
      }
      String newFilename = UUID.randomUUID().toString() + extension;
      Path dir = Paths.get(IMAGE_DIR);
      if (!Files.exists(dir)) Files.createDirectories(dir);
      Path filePath = dir.resolve(newFilename);
      file.transferTo(filePath.toFile());

      String imageUrl = "/api/images/" + newFilename;
      log.info("Изображение сохранено: {}", filePath);
      return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    } catch (IOException e) {
      log.error("Ошибка загрузки изображения", e);
      return ResponseEntity.internalServerError().body(Map.of("error", "Не удалось сохранить изображение"));
    }
  }

  @GetMapping("/images/{filename}")
  public ResponseEntity<Resource> getImage(@PathVariable String filename) {
    try {
      Path filePath = Paths.get(IMAGE_DIR).resolve(filename).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists() && resource.isReadable()) {
        String mimeType = "image/jpeg";
        if (filename.toLowerCase().endsWith(".png")) mimeType = "image/png";
        else if (filename.toLowerCase().endsWith(".gif")) mimeType = "image/gif";
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mimeType))
            .body(resource);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (MalformedURLException e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}