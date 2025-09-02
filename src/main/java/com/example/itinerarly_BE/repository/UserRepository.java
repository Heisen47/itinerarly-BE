package com.example.itinerarly_BE.repository;

import com.example.itinerarly_BE.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthId(String oauthId);
    Optional<User> findByEmail(String email);

    @Modifying
    void deleteByLoginTimeBefore(ZonedDateTime cutoff);
}