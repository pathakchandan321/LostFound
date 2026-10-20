package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.Claim;
import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.ClaimRepo;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClaimService {
    private final ClaimRepo claimRepo;
    private final LostItemRepo lostRepo;
    private final FoundItemRepo foundRepo;

    public ClaimService(ClaimRepo claimRepo, LostItemRepo lostRepo, FoundItemRepo foundRepo) {
        this.claimRepo = claimRepo;
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
    }

    @Transactional
    public Claim create(Long lostId, Long foundId, User claimant, String message) {
        LostItem lost = lostRepo.findById(lostId).orElseThrow(() -> new ResourceNotFoundException("Lost item not found"));
        FoundItem found = foundRepo.findById(foundId).orElseThrow(() -> new ResourceNotFoundException("Found item not found"));
        if (lost.getReporter() == null || !lost.getReporter().getId().equals(claimant.getId())) {
            throw new ResourceNotFoundException("Lost item not found");
        }
        if (!("LOST".equals(lost.getStatus()) || "MATCHED".equals(lost.getStatus()))
                || !("FOUND".equals(found.getStatus()) || "MATCHED".equals(found.getStatus()))) {
            throw new IllegalArgumentException("This item is no longer available for claiming");
        }
        if (claimRepo.findByClaimantIdOrFoundItemReporterIdOrderByClaimedAtDesc(claimant.getId(), claimant.getId())
                .stream()
                .anyMatch(existing -> existing.getLostItem() != null
                        && existing.getFoundItem() != null
                        && existing.getLostItem().getId().equals(lostId)
                        && existing.getFoundItem().getId().equals(foundId)
                        && !"REJECTED".equals(existing.getStatus()))) {
            throw new IllegalArgumentException("A claim already exists for this item");
        }
        Claim claim = new Claim();
        claim.setLostItem(lost);
        claim.setFoundItem(found);
        claim.setClaimant(claimant);
        claim.setMessage(message);
        lost.setStatus("CLAIMED");
        found.setStatus("CLAIMED");
        lostRepo.save(lost);
        foundRepo.save(found);
        return claimRepo.save(claim);
    }

    public List<Claim> findAll() {
        return claimRepo.findAll();
    }

    public List<Claim> findFor(User user) {
        return claimRepo.findByClaimantIdOrFoundItemReporterIdOrderByClaimedAtDesc(user.getId(), user.getId());
    }

    @Transactional
    public Claim approve(Long id) {
        Claim claim = get(id);
        if (!"PENDING".equals(claim.getStatus())) {
            throw new IllegalArgumentException("Only pending claims can be approved");
        }
        claim.setStatus("HANDOVER_PENDING");
        if (claim.getLostItem() != null) {
            claim.getLostItem().setStatus("CLAIMED");
            lostRepo.save(claim.getLostItem());
        }
        if (claim.getFoundItem() != null) {
            claim.getFoundItem().setStatus("CLAIMED");
            foundRepo.save(claim.getFoundItem());
        }
        return claimRepo.save(claim);
    }

    @Transactional
    public Claim completeHandover(Long id) {
        Claim claim = get(id);
        if (!("APPROVED".equals(claim.getStatus()) || "HANDOVER_PENDING".equals(claim.getStatus()))) {
            throw new IllegalArgumentException("Only approved claims can be handed over");
        }
        claim.setStatus("HANDOVER_COMPLETED");
        if (claim.getLostItem() != null) {
            claim.getLostItem().setStatus("RETURNED");
            lostRepo.save(claim.getLostItem());
        }
        if (claim.getFoundItem() != null) {
            claim.getFoundItem().setStatus("RETURNED");
            foundRepo.save(claim.getFoundItem());
        }
        return claimRepo.save(claim);
    }

    public Claim reject(Long id) {
        Claim claim = get(id);
        if (!"PENDING".equals(claim.getStatus())) {
            throw new IllegalArgumentException("Only pending claims can be rejected");
        }
        claim.setStatus("REJECTED");
        if (claim.getLostItem() != null) {
            claim.getLostItem().setStatus("MATCHED");
            lostRepo.save(claim.getLostItem());
        }
        if (claim.getFoundItem() != null) {
            claim.getFoundItem().setStatus("MATCHED");
            foundRepo.save(claim.getFoundItem());
        }
        return claimRepo.save(claim);
    }

    public void delete(Long id) {
        if (!claimRepo.existsById(id)) {
            throw new ResourceNotFoundException("Claim not found");
        }
        claimRepo.deleteById(id);
    }

    private Claim get(Long id) {
        return claimRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
    }
}
