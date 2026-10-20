package com.lostFound.lostFound.repository;

import com.lostFound.lostFound.Entity.SuccessStory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuccessStoryRepo extends JpaRepository<SuccessStory, Long> {
    List<SuccessStory> findAllByOrderByCreatedAtDesc();
    Optional<SuccessStory> findByClaimId(Long claimId);
}
