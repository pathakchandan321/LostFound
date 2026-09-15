package com.lostFound.lostFound.service;

import com.lostFound.lostFound.Entity.*;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ClaimService {
    private static final double APPROVAL_THRESHOLD = 0.70;
    private final ClaimRepo claimRepo; private final ItemMatchRepo matchRepo; private final LostItemRepo lostRepo; private final FoundItemRepo foundRepo;
    public ClaimService(ClaimRepo claimRepo, ItemMatchRepo matchRepo, LostItemRepo lostRepo, FoundItemRepo foundRepo) { this.claimRepo=claimRepo; this.matchRepo=matchRepo; this.lostRepo=lostRepo; this.foundRepo=foundRepo; }
    @Transactional public Claim startVerification(Long matchId, User claimant) {
        ItemMatch match = getMatch(matchId); requireOwner(match, claimant); if (!"SUGGESTED".equals(match.getStatus()) && !"VERIFICATION_PENDING".equals(match.getStatus())) throw new IllegalArgumentException("This match is no longer available.");
        return claimRepo.findByItemMatchIdAndClaimantId(matchId, claimant.getId()).orElseGet(() -> { Claim claim=new Claim(); claim.setItemMatch(match); claim.setClaimant(claimant); claim.setVerificationQuestions(questionsFor(match.getFoundItem())); match.setStatus("VERIFICATION_PENDING"); matchRepo.save(match); return claimRepo.save(claim); });
    }
    @Transactional public Claim submitVerification(Long claimId, User claimant, List<String> answers) {
        Claim claim=get(claimId); if(!claim.getClaimant().getId().equals(claimant.getId())) throw new ResourceNotFoundException("Verification case not found"); if(!"VERIFICATION_PENDING".equals(claim.getStatus())) throw new IllegalArgumentException("This verification is already closed.");
        String combined=String.join("\n", answers.stream().filter(Objects::nonNull).toList()); double score=verificationScore(combined, claim.getFoundItem().getPrivateDetails()); claim.setVerificationAnswers(combined); claim.setVerificationScore(score); claim.setStatus(score >= APPROVAL_THRESHOLD ? "PENDING_ADMIN_REVIEW" : "VERIFICATION_FAILED"); claim.getItemMatch().setStatus(score >= APPROVAL_THRESHOLD ? "PENDING_ADMIN_REVIEW" : "SUGGESTED"); matchRepo.save(claim.getItemMatch()); return claimRepo.save(claim);
    }
    @Transactional public Claim approve(Long id) { Claim claim=get(id); if(!"PENDING_ADMIN_REVIEW".equals(claim.getStatus())) throw new IllegalArgumentException("Only verified cases can be approved."); closeApproved(claim); return claimRepo.save(claim); }
    @Transactional public Claim reject(Long id) { Claim claim=get(id); if("APPROVED".equals(claim.getStatus())) throw new IllegalArgumentException("An approved case cannot be rejected."); claim.setStatus("REJECTED"); claim.getItemMatch().setStatus("REJECTED"); matchRepo.save(claim.getItemMatch()); return claimRepo.save(claim); }
    public List<Claim> findFor(User user) { return claimRepo.findByClaimantIdOrderByClaimedAtDesc(user.getId()); }
    public List<Claim> pendingAdminReview() { return claimRepo.findByStatus("PENDING_ADMIN_REVIEW"); }
    public ItemMatch previewFor(Long matchId, User user) { ItemMatch match=getMatch(matchId); requireOwner(match,user); return match; }
    private void closeApproved(Claim claim) { ItemMatch match=claim.getItemMatch(); LostItem lost=match.getLostItem(); FoundItem found=match.getFoundItem(); claim.setStatus("APPROVED"); match.setStatus("APPROVED"); lost.setStatus("RECOVERED"); lost.setActive(false); found.setStatus("CLAIMED"); found.setActive(false); lostRepo.save(lost); foundRepo.save(found); matchRepo.save(match); matchRepo.findByLostItemIdOrFoundItemId(lost.getId(), found.getId()).stream().filter(other -> !other.getId().equals(match.getId())).forEach(other -> { other.setStatus("CLOSED"); matchRepo.save(other); }); }
    private String questionsFor(FoundItem item) { String detail=item.getPrivateDetails().toLowerCase(Locale.ROOT); List<String> questions=new ArrayList<>(); questions.add("Describe a unique mark, label, scratch, or feature only the owner would know."); if(detail.matches(".*\\d+.*")) questions.add("How many items, parts, or contents should be associated with it?"); else questions.add("What specific color, pattern, or material detail can you confirm?"); questions.add("Provide another identifying detail that was not included in your original lost report."); return String.join("\n", questions); }
    private double verificationScore(String answers, String expected) { Set<String> answerTokens=tokens(answers); Set<String> expectedTokens=tokens(expected); if(answerTokens.isEmpty() || expectedTokens.isEmpty()) return 0; Set<String> overlap=new HashSet<>(answerTokens); overlap.retainAll(expectedTokens); double recall=(double)overlap.size()/expectedTokens.size(); double precision=(double)overlap.size()/answerTokens.size(); return Math.min(1, .70*recall + .30*precision); }
    private Set<String> tokens(String value) { Set<String> set=new HashSet<>(); for(String token:value.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) if(token.length()>2) set.add(token); return set; }
    private Claim get(Long id) { return claimRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Verification case not found")); }
    private ItemMatch getMatch(Long id) { return matchRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Match not found")); }
    private void requireOwner(ItemMatch match, User user) { if(!match.getLostItem().getReporter().getId().equals(user.getId())) throw new ResourceNotFoundException("Match not found"); }
}