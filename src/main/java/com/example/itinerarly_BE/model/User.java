package com.example.itinerarly_BE.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oauthId;
    private String email;
    private String name;
    private String username;
    private String avatarUrl;
    private String provider;


    @Column(name = "daily_tokens")
    private Integer dailyTokens ;

    @Column(name = "last_token_refresh")
    private LocalDate lastTokenRefresh = LocalDate.now();

}
