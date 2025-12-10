package com.lostFound.lostFound.repository;

import com.lostFound.lostFound.Entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepo extends JpaRepository<Claim, Long> {
    List<Claim> findByStatus(String status);
}
