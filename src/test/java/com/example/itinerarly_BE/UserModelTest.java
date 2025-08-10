package com.example.itinerarly_BE;

import com.example.itinerarly_BE.model.User;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserModelTest {
    @Test
    void userModelShouldStoreAndRetrieveFields() {
        User user = new User();
        user.setId(1L);
        user.setOauthId("oauth-123");
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setUsername("testuser");
        user.setAvatarUrl("http://avatar.com/img.png");
        user.setProvider("google");
        user.setDailyTokens(6);


        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getOauthId()).isEqualTo("oauth-123");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getAvatarUrl()).isEqualTo("http://avatar.com/img.png");
        assertThat(user.getProvider()).isEqualTo("google");
        assertThat(user.getDailyTokens()).isEqualTo(6);
    }
}

