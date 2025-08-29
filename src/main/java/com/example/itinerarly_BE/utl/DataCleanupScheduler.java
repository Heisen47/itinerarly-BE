package com.example.itinerarly_BE.utl;

import com.example.itinerarly_BE.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataCleanupScheduler {
    private final UserRepository userRepository;

    public DataCleanupScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Runs every 3 days (259200000 ms)
    @Scheduled(fixedRate = 259200000)
    public void deleteOldData() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(3);
        userRepository.deleteByCreatedAtBefore(cutoff);
        System.out.println("Old data deleted before: " + cutoff);
    }
}
