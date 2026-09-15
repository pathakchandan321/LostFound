package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.*;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {
    private final LostItemRepo lostRepo; private final FoundItemRepo foundRepo; private final FileStorageService storage; private final MatchingService matching;
    public ItemService(LostItemRepo lostRepo, FoundItemRepo foundRepo, FileStorageService storage, MatchingService matching) { this.lostRepo=lostRepo; this.foundRepo=foundRepo; this.storage=storage; this.matching=matching; }
    @Transactional public LostItem createLost(LostItem item, MultipartFile image, User reporter) {
        requireImage(image); item.setReporter(reporter); if(item.getOccurredAt()==null) item.setOccurredAt(LocalDateTime.now()); item.setDateLost(item.getOccurredAt().toLocalDate()); item.setStatus("LOST"); item.setActive(true); item.setImageHash(storage.fingerprint(image)); item.setImagePath(storage.store(image)); LostItem saved=lostRepo.save(item); matching.createMatchesForLost(saved); return saved;
    }
    @Transactional public FoundItem createFound(FoundItem item, MultipartFile image, User reporter) {
        requireImage(image); if(item.getPrivateDetails()==null || item.getPrivateDetails().isBlank()) throw new IllegalArgumentException("Private identifying details are required for ownership verification."); item.setReporter(reporter); if(item.getOccurredAt()==null) item.setOccurredAt(LocalDateTime.now()); item.setDateFound(item.getOccurredAt().toLocalDate()); item.setStatus("FOUND"); item.setActive(true); item.setImageHash(storage.fingerprint(image)); item.setImagePath(storage.store(image)); FoundItem saved=foundRepo.save(item); matching.createMatchesForFound(saved); return saved;
    }
    public List<LostItem> lostItemsFor(User user) { return lostRepo.findByReporterIdOrderByCreatedAtDesc(user.getId()); }
    public List<FoundItem> foundItemsFor(User user) { return foundRepo.findByReporterIdOrderByCreatedAtDesc(user.getId()); }
    public LostItem lostById(Long id) { return lostRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lost report not found")); }
    public FoundItem foundById(Long id) { return foundRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Found report not found")); }
    private void requireImage(MultipartFile image) { if(image == null || image.isEmpty()) throw new IllegalArgumentException("A photo is required for automatic matching."); }
}