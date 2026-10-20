package com.lostFound.lostFound.repository;

import com.lostFound.lostFound.Entity.FoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoundItemRepo extends JpaRepository<FoundItem, Long> {
    List<FoundItem> findByStatus(String status);
    List<FoundItem> findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location);
    List<FoundItem> findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String description, String location);
    List<FoundItem> findByReporterIdOrderByDateFoundDesc(Long reporterId);
    Optional<FoundItem> findByImagePath(String imagePath);
    @Query("select f from FoundItem f where (lower(f.itemName) like lower(concat('%', :name, '%')) " +
            "or lower(f.location) like lower(concat('%', :location, '%'))) and f.status <> :status")
    List<FoundItem> findPotentialMatches(@Param("name") String name, @Param("location") String location,
                                         @Param("status") String status);
}
