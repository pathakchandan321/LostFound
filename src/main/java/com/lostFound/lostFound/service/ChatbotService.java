package com.lostFound.lostFound.service;

import org.springframework.stereotype.Service;

@Service
public class ChatbotService {
    public String reply(String message) {
        String text = message == null ? "" : message.toLowerCase();
        if (text.contains("lost") || text.contains("report")) {
            return "To report a lost item, open Report Lost Item, enter the item name, location, description, and upload a photo if available. The system will search for similar found items automatically.";
        }
        if (text.contains("found")) {
            return "To report a found item, open Report Found Item, add where you found it, describe identifying details, and upload a clear image. The owner can then raise a claim.";
        }
        if (text.contains("claim")) {
            return "To claim an item, open Found Items, choose the matching found item, provide your lost item ID and a short proof message. An admin will approve or reject the claim.";
        }
        if (text.contains("status") || text.contains("track")) {
            return "Statuses mean: LOST or FOUND is newly posted, MATCHED has similar items, CLAIMED is waiting for admin review, and RETURNED means the claim was approved.";
        }
        if (text.contains("admin")) {
            return "Admins can review claims, approve or reject them, and manage users and item posts from the Admin page.";
        }
        return "I can help with reporting lost items, posting found items, claiming items, tracking status, and common admin questions.";
    }
}
