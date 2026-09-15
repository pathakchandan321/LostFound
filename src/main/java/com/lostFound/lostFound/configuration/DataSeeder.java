package com.lostFound.lostFound.configuration;

import com.lostFound.lostFound.Entity.*;
import com.lostFound.lostFound.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataSeeder {
 @Bean CommandLineRunner seedData(UserRepo users,LostItemRepo lost,FoundItemRepo found,PasswordEncoder encoder){return args->{
  User admin=users.findByUsername("admin").orElseGet(()->{User u=new User();u.setUsername("admin");u.setEmail("admin@college.local");u.setPassword(encoder.encode("admin123"));u.setRole("ADMIN");return users.save(u);});
  User student=users.findByUsername("student").orElseGet(()->{User u=new User();u.setUsername("student");u.setEmail("student@college.local");u.setPassword(encoder.encode("student123"));u.setRole("USER");return users.save(u);});
  if(lost.count()==0){LostItem item=new LostItem();item.setItemName("Blue Water Bottle");item.setDescription("Steel bottle with a name sticker near the cap.");item.setCategory("Personal item");item.setLocation("Library second floor");item.setOccurredAt(LocalDateTime.now().minusDays(2));item.setDateLost(LocalDate.now().minusDays(2));item.setReporter(student);lost.save(item);}
  if(found.count()==0){FoundItem item=new FoundItem();item.setItemName("Blue Water Bottle");item.setDescription("Steel bottle found after morning class.");item.setCategory("Personal item");item.setLocation("Library second floor");item.setPrivateDetails("Silver cap with initials CP and a small dent beside the handle.");item.setOccurredAt(LocalDateTime.now().minusDays(1));item.setDateFound(LocalDate.now().minusDays(1));item.setReporter(admin);found.save(item);}
 };}
}
