package com.lostFound.lostFound.repository;

import com.lostFound.lostFound.Entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostItemRepo extends JpaRepository<LostItem, Long> {
    List<LostItem> findByStatus(String status);
    List<LostItem> findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location);
}
