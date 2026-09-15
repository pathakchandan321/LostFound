package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.*;
import com.lostFound.lostFound.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MatchingService {
    public static final double MATCH_THRESHOLD = 0.65;
    private static final Pattern TOKEN = Pattern.compile("[^a-z0-9]+");
    private final LostItemRepo lostRepo; private final FoundItemRepo foundRepo; private final ItemMatchRepo matchRepo; private final NotificationRepo notificationRepo;
    public MatchingService(LostItemRepo lostRepo, FoundItemRepo foundRepo, ItemMatchRepo matchRepo, NotificationRepo notificationRepo) { this.lostRepo=lostRepo; this.foundRepo=foundRepo; this.matchRepo=matchRepo; this.notificationRepo=notificationRepo; }
    @Transactional public void createMatchesForLost(LostItem lost) { foundRepo.findByActiveTrue().forEach(found -> createIfConfident(lost, found)); }
    @Transactional public void createMatchesForFound(FoundItem found) { lostRepo.findByActiveTrue().forEach(lost -> createIfConfident(lost, found)); }
    private void createIfConfident(LostItem lost, FoundItem found) {
        if (!lost.isActive() || !found.isActive() || matchRepo.findByLostItemIdAndFoundItemId(lost.getId(), found.getId()).isPresent()) return;
        Scores scores = score(lost, found);
        if (scores.total < MATCH_THRESHOLD) return;
        ItemMatch match = new ItemMatch(); match.setLostItem(lost); match.setFoundItem(found); match.setConfidenceScore(scores.total); match.setImageScore(scores.image); match.setKeywordScore(scores.keyword); match.setSemanticScore(scores.semantic); match.setCategoryScore(scores.category); match.setLocationScore(scores.location); match.setTimeScore(scores.time);
        ItemMatch saved = matchRepo.save(match);
        if (!notificationRepo.existsByRecipientIdAndItemMatchId(lost.getReporter().getId(), saved.getId())) {
            Notification notification = new Notification(); notification.setRecipient(lost.getReporter()); notification.setItemMatch(saved); notification.setMessage("We found a similar item. Could this be yours?"); notificationRepo.save(notification);
        }
    }
    private Scores score(LostItem lost, FoundItem found) {
        double image = imageSimilarity(lost.getImageHash(), found.getImageHash());
        double keyword = overlap(tokens(lost.getItemName()), tokens(found.getItemName()));
        double semantic = overlap(tokens(lost.getDescription()), tokens(found.getDescription()));
        double category = normalize(lost.getCategory()).equals(normalize(found.getCategory())) ? 1 : overlap(tokens(lost.getCategory()), tokens(found.getCategory()));
        double location = overlap(tokens(lost.getLocation()), tokens(found.getLocation()));
        long hours = Math.abs(ChronoUnit.HOURS.between(occurredAt(lost), occurredAt(found))); double time = Math.max(0, 1 - hours / (24.0 * 45));
        double weight = 0.20 + 0.25 + 0.15 + 0.07 + 0.03; double total = keyword*.20 + semantic*.25 + category*.15 + location*.07 + time*.03;
        if (image >= 0) { total += image*.30; weight += .30; }
        return new Scores(total / weight, Math.max(0, image), keyword, semantic, category, location, time);
    }
    private double imageSimilarity(String first, String second) { if (first == null || second == null || first.length() != second.length()) return -1; int same=0; for(int i=0;i<first.length();i++) if(first.charAt(i)==second.charAt(i)) same++; return (double)same/first.length(); }
    private LocalDateTime occurredAt(LostItem item) { return item.getOccurredAt() != null ? item.getOccurredAt() : dateOrCreatedAt(item.getDateLost(), item.getCreatedAt()); }
    private LocalDateTime occurredAt(FoundItem item) { return item.getOccurredAt() != null ? item.getOccurredAt() : dateOrCreatedAt(item.getDateFound(), item.getCreatedAt()); }
    private LocalDateTime dateOrCreatedAt(LocalDate date, LocalDateTime createdAt) { return date != null ? date.atStartOfDay() : createdAt != null ? createdAt : LocalDateTime.now(); }
    private Set<String> tokens(String text) { Set<String> result=new HashSet<>(); for(String token:TOKEN.split(normalize(text))) if(token.length()>2) result.add(token); return result; }
    private String normalize(String value) { return value == null ? "" : value.toLowerCase(Locale.ROOT).trim(); }
    private double overlap(Set<String> a, Set<String> b) { if(a.isEmpty() || b.isEmpty()) return 0; Set<String> union=new HashSet<>(a); union.addAll(b); Set<String> intersection=new HashSet<>(a); intersection.retainAll(b); return (double)intersection.size()/union.size(); }
    private record Scores(double total, double image, double keyword, double semantic, double category, double location, double time) {}
}
