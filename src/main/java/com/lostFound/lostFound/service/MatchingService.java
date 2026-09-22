package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.Entity.Notification;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import com.lostFound.lostFound.repository.NotificationRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MatchingService {
    private final LostItemRepo lostRepo;
    private final FoundItemRepo foundRepo;
    private final NotificationRepo notificationRepo;

    public MatchingService(LostItemRepo lostRepo, FoundItemRepo foundRepo, NotificationRepo notificationRepo) {
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
        this.notificationRepo = notificationRepo;
    }

    public List<FoundItem> findMatchesForLost(LostItem lost) {
        return foundRepo.findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(lost.getItemName(), lost.getLocation());
    }

    public List<LostItem> findMatchesForFound(FoundItem found) {
        return lostRepo.findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(found.getItemName(), found.getLocation());
    }

    @Transactional
    public void notifyMatchesForLost(LostItem lost, List<FoundItem> matches) {
        if (matches.isEmpty()) return;
        notifyUser(lost.getReporter(), "A similar found-item report was submitted. For safety, all item details remain hidden until verification.");
        matches.stream().map(FoundItem::getReporter).filter(user -> !sameUser(user, lost.getReporter())).forEach(user -> notifyUser(user, "A similar lost-item report was submitted. For safety, all item details remain hidden until verification."));
    }

    @Transactional
    public void notifyMatchesForFound(FoundItem found, List<LostItem> matches) {
        if (matches.isEmpty()) return;
        notifyUser(found.getReporter(), "A similar lost-item report was submitted. For safety, all item details remain hidden until verification.");
        matches.stream().map(LostItem::getReporter).filter(user -> !sameUser(user, found.getReporter())).forEach(user -> notifyUser(user, "A similar found-item report was submitted. For safety, all item details remain hidden until verification."));
    }

    private void notifyUser(User user, String message) {
        if (user == null) return;
        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setMessage(message);
        notificationRepo.save(notification);
    }

    private boolean sameUser(User first, User second) {
        return first != null && second != null && first.getId().equals(second.getId());
    }
}
