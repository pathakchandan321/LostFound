package com.lostFound.lostFound.dto;

import com.lostFound.lostFound.Entity.Claim;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ClaimDtos {
    public record ClaimRequest(@NotNull Long lostItemId, @NotNull Long foundItemId, String message) {
    }

    public record ClaimResponse(Long id, Long lostItemId, String lostItemName, Long foundItemId,
                                String foundItemName, String claimant, String message,
                                String status, LocalDateTime claimedAt) {
        public static ClaimResponse from(Claim claim) {
            return new ClaimResponse(
                    claim.getId(),
                    claim.getLostItem() == null ? null : claim.getLostItem().getId(),
                    claim.getLostItem() == null ? null : claim.getLostItem().getItemName(),
                    claim.getFoundItem() == null ? null : claim.getFoundItem().getId(),
                    claim.getFoundItem() == null ? null : claim.getFoundItem().getItemName(),
                    claim.getClaimant() == null ? null : claim.getClaimant().getUsername(),
                    claim.getMessage(),
                    claim.getStatus(),
                    claim.getClaimedAt());
        }
    }
}
