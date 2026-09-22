package com.lostFound.lostFound.repository;

import com.lostFound.lostFound.Entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LostItemRepo extends JpaRepository<LostItem, Long> {
    List<LostItem> findByStatus(String status);
    List<LostItem> findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location);
    List<LostItem> findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String description, String location);
    List<LostItem> findByReporterIdOrderByDateLostDesc(Long reporterId);
    Optional<LostItem> findByImagePath(String imagePath);
}
