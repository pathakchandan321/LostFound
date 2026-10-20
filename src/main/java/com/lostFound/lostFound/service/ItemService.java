package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @Transactional
    public LostItem createLost(LostItem item, MultipartFile image, User reporter) {
        item.setReporter(reporter);
        item.setDateLost(LocalDate.now());
        item.setStatus("LOST");
        item.setImagePath(fileStorageService.store(image));
        LostItem saved = lostRepo.save(item);
        List<FoundItem> matches = matchingService.findMatchesForLost(saved);
        if (!matches.isEmpty()) {
            saved.setStatus("MATCHED");
            saved = lostRepo.save(saved);
            matchingService.notifyMatchesForLost(saved, matches);
        }
        return saved;
    }

    @Transactional
    public FoundItem createFound(FoundItem item, MultipartFile image, User reporter) {
        item.setReporter(reporter);
        item.setDateFound(LocalDate.now());
        item.setStatus("FOUND");
        item.setImagePath(fileStorageService.store(image));
        FoundItem saved = foundRepo.save(item);
        List<LostItem> matches = matchingService.findMatchesForFound(saved);
        if (!matches.isEmpty()) {
            saved.setStatus("MATCHED");
            saved = foundRepo.save(saved);
            matchingService.notifyMatchesForFound(saved, matches);
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

    public List<LostItem> lostItemsFor(User user) {
        return lostRepo.findByReporterIdOrderByDateLostDesc(user.getId());
    }

    public List<FoundItem> foundItemsFor(User user) {
        return foundRepo.findByReporterIdOrderByDateFoundDesc(user.getId());
    }

    public List<FoundItem> potentialFoundItemsFor(User user) {
        Map<Long, FoundItem> matches = new LinkedHashMap<>();
        lostItemsFor(user).forEach(lost -> foundRepo
                .findPotentialMatches(lost.getItemName(), lost.getDescription(), lost.getLocation())
                .stream()
                .filter(found -> "LOST".equals(lost.getStatus()) || "MATCHED".equals(lost.getStatus()))
                .forEach(found -> matches.put(found.getId(), found)));
        return List.copyOf(matches.values());
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
