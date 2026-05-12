package com.example.murinofm.repository;

import com.example.murinofm.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
  boolean existsByUsername(String username);
  List<AppUser> findByUsernameContainingIgnoreCase(String username);

}
