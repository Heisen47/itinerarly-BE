package com.example.itinerarly_BE.repository;

import com.example.itinerarly_BE.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve user by oauthId")
    void testSaveAndFindByOauthId() {
        User user = new User();
        user.setOauthId("oauth-456");
        user.setEmail("repo@example.com");
        user.setName("Repo User");
        user.setUsername("repouser");
        user.setProvider("github");
        user.setDailyTokens(6);
        userRepository.save(user);

        Optional<User> found = userRepository.findByOauthId("oauth-456");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("repouser");
    }

    @Test
    @DisplayName("Should save and retrieve user by email")
    void testSaveAndFindByEmail() {
        User user = new User();
        user.setOauthId("oauth-789");
        user.setEmail("findbyemail@example.com");
        user.setName("Email User");
        user.setUsername("emailuser");
        user.setProvider("google");
        user.setDailyTokens(6);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("findbyemail@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getOauthId()).isEqualTo("oauth-789");
    }
}

