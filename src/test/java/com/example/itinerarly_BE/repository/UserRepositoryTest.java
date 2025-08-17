package com.example.itinerarly_BE.repository;

import com.example.itinerarly_BE.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setOauthId("test-oauth-id");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setProvider("google");
        testUser.setAvatarUrl("https://example.com/avatar.jpg");
        testUser.setDailyTokens(10);
        testUser.setLastTokenRefresh(LocalDate.now());
        testUser.setLoginTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
    }

    @Test
    void testFindByOauthId_Found() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> found = userRepository.findByOauthId("test-oauth-id");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("Test User", found.get().getName());
        assertEquals("google", found.get().getProvider());
    }

    @Test
    void testFindByOauthId_NotFound() {
        // When
        Optional<User> found = userRepository.findByOauthId("nonexistent-oauth-id");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveUser_Success() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("test-oauth-id", savedUser.getOauthId());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test User", savedUser.getName());
    }

    @Test
    void testFindByEmail_Found() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test-oauth-id", found.get().getOauthId());
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);

        // When
        savedUser.setDailyTokens(5);
        savedUser.setName("Updated Name");
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals(5, updatedUser.getDailyTokens());
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();

        // When
        userRepository.delete(savedUser);
        entityManager.flush();

        // Then
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }
}
