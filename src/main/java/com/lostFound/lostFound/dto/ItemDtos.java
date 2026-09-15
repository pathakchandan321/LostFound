package com.lostFound.lostFound.dto;

import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class ItemDtos {
    public record ItemRequest(@NotBlank String itemName,@NotBlank String description,@NotBlank String category,@NotBlank String location) {}
    public record FoundItemRequest(@NotBlank String itemName,@NotBlank String description,@NotBlank String category,@NotBlank String location,@NotBlank String privateDetails) {}
    public record LostItemResponse(Long id,String itemName,String description,String category,String location,LocalDate dateLost,String status) { public static LostItemResponse from(LostItem item){return new LostItemResponse(item.getId(),item.getItemName(),item.getDescription(),item.getCategory(),item.getLocation(),item.getDateLost(),item.getStatus());} }
    public record FoundItemResponse(Long id,String itemName,String description,String category,String location,LocalDate dateFound,String status) { public static FoundItemResponse from(FoundItem item){return new FoundItemResponse(item.getId(),item.getItemName(),item.getDescription(),item.getCategory(),item.getLocation(),item.getDateFound(),item.getStatus());} }
}