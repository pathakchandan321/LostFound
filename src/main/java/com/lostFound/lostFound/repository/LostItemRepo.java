package com.lostFound.lostFound.repository;
import com.lostFound.lostFound.Entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface LostItemRepo extends JpaRepository<LostItem, Long> {
    List<LostItem> findByReporterIdOrderByCreatedAtDesc(Long reporterId);
    List<LostItem> findByActiveTrue();
    Optional<LostItem> findByImagePath(String imagePath);
}