package com.lostFound.lostFound.repository;
import com.lostFound.lostFound.Entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ClaimRepo extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimantIdOrderByClaimedAtDesc(Long claimantId);
    Optional<Claim> findByItemMatchIdAndClaimantId(Long matchId, Long claimantId);
    List<Claim> findByStatus(String status);
}