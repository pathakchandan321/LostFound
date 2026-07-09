package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingService {

    private final LostItemRepo lostRepo;
    private final FoundItemRepo foundRepo;

    public MatchingService(LostItemRepo lostRepo, FoundItemRepo foundRepo) {
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
    }

    public List<FoundItem> findMatchesForLost(LostItem lost) {
        return foundRepo.findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                lost.getItemName(), lost.getLocation());
    }

    public List<LostItem> findMatchesForFound(FoundItem found) {
        return lostRepo.findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                found.getItemName(), found.getLocation());
    }
}