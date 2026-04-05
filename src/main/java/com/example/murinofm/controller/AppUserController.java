package com.example.murinofm.controller;

import com.example.murinofm.dto.AppUserDto;
import com.example.murinofm.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

  private final AppUserService appUserService;

  // GET /api/users — все пользователи
  @GetMapping
  public List<AppUserDto> getAllUsers() {
    return appUserService.getAllUsers();
  }

  // GET /api/users/{id} — один пользователь по ID
  @GetMapping("/{id}")
  public AppUserDto getUserById(@PathVariable Long id) {
    return appUserService.getUserById(id);
  }

  // POST /api/users?username=Ivan — создать
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AppUserDto createUser(@RequestParam String username) {
    return appUserService.createUser(username);
  }

  // PUT /api/users/{id}?username=NewName — обновить имя
  @PutMapping("/{id}")
  public AppUserDto updateUser(@PathVariable Long id, @RequestParam String username) {
    return appUserService.updateUsername(id, username);
  }

  // DELETE /api/users/{id} — удалить
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    appUserService.deleteUser(id);
  }
}