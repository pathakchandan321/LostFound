package com.lostFound.lostFound.configuration;

import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import com.lostFound.lostFound.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedData(UserRepo userRepo, LostItemRepo lostRepo,
                               FoundItemRepo foundRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            User admin = userRepo.findByUsername("admin").orElseGet(() -> {
                User user = new User();
                user.setUsername("admin");
                user.setEmail("admin@college.local");
                user.setPassword(passwordEncoder.encode("admin123"));
                user.setRole("ADMIN");
                return userRepo.save(user);
            });

            User student = userRepo.findByUsername("student").orElseGet(() -> {
                User user = new User();
                user.setUsername("student");
                user.setEmail("student@college.local");
                user.setPassword(passwordEncoder.encode("student123"));
                user.setRole("USER");
                return userRepo.save(user);
            });

            if (lostRepo.count() == 0) {
                LostItem bottle = new LostItem();
                bottle.setItemName("Blue Water Bottle");
                bottle.setDescription("Steel bottle with a name sticker near the cap.");
                bottle.setLocation("Library second floor");
                bottle.setDateLost(LocalDate.now().minusDays(2));
                bottle.setStatus("LOST");
                bottle.setReporter(student);
                lostRepo.save(bottle);
            }

            if (foundRepo.count() == 0) {
                FoundItem keys = new FoundItem();
                keys.setItemName("Keychain with two keys");
                keys.setDescription("Black keychain found after morning class.");
                keys.setLocation("Computer lab");
                keys.setDateFound(LocalDate.now().minusDays(1));
                keys.setStatus("FOUND");
                keys.setReporter(admin);
                foundRepo.save(keys);
            }
        };
    }
}
