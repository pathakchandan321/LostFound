package com.lostFound.lostFound.dto;

import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class ItemDtos {
    public record ItemRequest(
            @NotBlank String itemName,
            @NotBlank String description,
            @NotBlank String location) {
    }

    public record LostItemResponse(Long id, String itemName, String description, String location,
                                   LocalDate dateLost, String imagePath, String status, String reporter) {
        public static LostItemResponse from(LostItem item) {
            return new LostItemResponse(
                    item.getId(),
                    item.getItemName(),
                    item.getDescription(),
                    item.getLocation(),
                    item.getDateLost(),
                    item.getImagePath(),
                    item.getStatus(),
                    item.getReporter() == null ? null : item.getReporter().getUsername());
        }
    }

    public record FoundItemResponse(Long id, String itemName, String description, String location,
                                    LocalDate dateFound, String imagePath, String status, String reporter) {
        public static FoundItemResponse from(FoundItem item) {
            return new FoundItemResponse(
                    item.getId(),
                    item.getItemName(),
                    item.getDescription(),
                    item.getLocation(),
                    item.getDateFound(),
                    item.getImagePath(),
                    item.getStatus(),
                    item.getReporter() == null ? null : item.getReporter().getUsername());
        }
    }
}
