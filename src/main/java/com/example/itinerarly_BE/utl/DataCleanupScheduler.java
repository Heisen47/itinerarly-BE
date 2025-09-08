package com.example.itinerarly_BE.utl;

import com.example.itinerarly_BE.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class DataCleanupScheduler {
    private final UserRepository userRepository;

    public DataCleanupScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Scheduled(fixedRate = 259200000)
    @Transactional
    public void deleteOldData() {
        ZonedDateTime cutoff = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).minusDays(3);
        userRepository.deleteByLoginTimeBefore(cutoff);
        System.out.println("Old data deleted before: " + cutoff);
    }
}
