package com.lostFound.lostFound.repository;

import com.lostFound.lostFound.Entity.FoundItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoundItemRepo extends JpaRepository<FoundItem, Long> {
    List<FoundItem> findByStatus(String status);
    List<FoundItem> findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location);
    List<FoundItem> findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String description, String location);
}
