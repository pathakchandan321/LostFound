package com.lostFound.lostFound.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne private LostItem lostItem;
    @ManyToOne private FoundItem foundItem;
    @ManyToOne private User claimant;

    private String status = "PENDING"; // PENDING, APPROVED, REJECTED
    private LocalDateTime claimedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LostItem getLostItem() {
        return lostItem;
    }

    public void setLostItem(LostItem lostItem) {
        this.lostItem = lostItem;
    }

    public FoundItem getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(FoundItem foundItem) {
        this.foundItem = foundItem;
    }

    public User getClaimant() {
        return claimant;
    }

    public void setClaimant(User claimant) {
        this.claimant = claimant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }

    // getters & setters
}

