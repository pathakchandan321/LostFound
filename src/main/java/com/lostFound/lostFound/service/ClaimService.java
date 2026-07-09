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

    public Claim create(Long lostId, Long foundId, User claimant, String message) {
        LostItem lost = lostRepo.findById(lostId).orElseThrow(() -> new ResourceNotFoundException("Lost item not found"));
        FoundItem found = foundRepo.findById(foundId).orElseThrow(() -> new ResourceNotFoundException("Found item not found"));
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

    public Claim approve(Long id) {
        Claim claim = get(id);
        claim.setStatus("APPROVED");
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
