package com.lostFound.lostFound.repository;
import com.lostFound.lostFound.Entity.FoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface FoundItemRepo extends JpaRepository<FoundItem, Long> {
    List<FoundItem> findByReporterIdOrderByCreatedAtDesc(Long reporterId);
    List<FoundItem> findByActiveTrue();
    Optional<FoundItem> findByImagePath(String imagePath);
}