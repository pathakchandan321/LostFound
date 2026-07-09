package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class ItemService {
    private final LostItemRepo lostRepo;
    private final FoundItemRepo foundRepo;
    private final FileStorageService fileStorageService;
    private final MatchingService matchingService;

    public ItemService(LostItemRepo lostRepo, FoundItemRepo foundRepo,
                       FileStorageService fileStorageService, MatchingService matchingService) {
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
        this.fileStorageService = fileStorageService;
        this.matchingService = matchingService;
    }

    public LostItem createLost(LostItem item, MultipartFile image, User reporter) {
        item.setReporter(reporter);
        item.setDateLost(LocalDate.now());
        item.setStatus("LOST");
        item.setImagePath(fileStorageService.store(image));
        LostItem saved = lostRepo.save(item);
        if (!matchingService.findMatchesForLost(saved).isEmpty()) {
            saved.setStatus("MATCHED");
            saved = lostRepo.save(saved);
        }
        return saved;
    }

    public FoundItem createFound(FoundItem item, MultipartFile image, User reporter) {
        item.setReporter(reporter);
        item.setDateFound(LocalDate.now());
        item.setStatus("FOUND");
        item.setImagePath(fileStorageService.store(image));
        FoundItem saved = foundRepo.save(item);
        if (!matchingService.findMatchesForFound(saved).isEmpty()) {
            saved.setStatus("MATCHED");
            saved = foundRepo.save(saved);
        }
        return saved;
    }

    public List<LostItem> lostItems(String query) {
        return query == null || query.isBlank()
                ? lostRepo.findAll()
                : lostRepo.findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(query, query, query);
    }

    public List<FoundItem> foundItems(String query) {
        return query == null || query.isBlank()
                ? foundRepo.findAll()
                : foundRepo.findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(query, query, query);
    }

    public LostItem lostById(Long id) {
        return lostRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lost item not found"));
    }

    public FoundItem foundById(Long id) {
        return foundRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Found item not found"));
    }

    public void deleteLost(Long id) {
        if (!lostRepo.existsById(id)) {
            throw new ResourceNotFoundException("Lost item not found");
        }
        lostRepo.deleteById(id);
    }

    public void deleteFound(Long id) {
        if (!foundRepo.existsById(id)) {
            throw new ResourceNotFoundException("Found item not found");
        }
        foundRepo.deleteById(id);
    }
}
