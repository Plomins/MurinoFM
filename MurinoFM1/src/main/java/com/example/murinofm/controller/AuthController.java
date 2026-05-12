package com.example.murinofm.controller;

import com.example.murinofm.dto.AppUserDto;
import com.example.murinofm.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AppUserService appUserService;

  @PostMapping("/login")
  public AppUserDto login(@RequestBody Map<String, String> credentials) {
    String username = credentials.get("username");
    String password = credentials.get("password");
    return appUserService.authenticate(username, password);
  }
}