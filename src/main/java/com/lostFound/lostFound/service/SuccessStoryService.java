package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.Claim;
import com.lostFound.lostFound.Entity.SuccessStory;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.ClaimRepo;
import com.lostFound.lostFound.repository.SuccessStoryRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SuccessStoryService {
    private final SuccessStoryRepo storyRepo;
    private final ClaimRepo claimRepo;
    private final FileStorageService fileStorageService;

    public SuccessStoryService(SuccessStoryRepo storyRepo, ClaimRepo claimRepo,
                               FileStorageService fileStorageService) {
        this.storyRepo = storyRepo;
        this.claimRepo = claimRepo;
        this.fileStorageService = fileStorageService;
    }

    public List<SuccessStory> findAll() {
        return storyRepo.findAllByOrderByCreatedAtDesc();
    }

    public boolean canSubmit(Long claimId, User author) {
        return claimRepo.findById(claimId)
                .filter(claim -> "APPROVED".equals(claim.getStatus()))
                .filter(claim -> claim.getFoundItem() != null && claim.getFoundItem().getReporter() != null)
                .filter(claim -> claim.getFoundItem().getReporter().getId().equals(author.getId()))
                .filter(claim -> storyRepo.findByClaimId(claimId).isEmpty())
                .isPresent();
    }

    public SuccessStory create(Long claimId, String storyText, MultipartFile image, User author) {
        Claim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        if (!"APPROVED".equals(claim.getStatus())
                || claim.getFoundItem() == null
                || claim.getFoundItem().getReporter() == null
                || !claim.getFoundItem().getReporter().getId().equals(author.getId())) {
            throw new ResourceNotFoundException("Success story is not available for this claim");
        }
        if (storyRepo.findByClaimId(claimId).isPresent()) {
            throw new IllegalArgumentException("A success story already exists for this claim");
        }
        SuccessStory successStory = new SuccessStory();
        successStory.setClaim(claim);
        successStory.setAuthor(author);
        successStory.setStory(storyText);
        successStory.setImagePath(fileStorageService.store(image));
        return storyRepo.save(successStory);
    }
}
