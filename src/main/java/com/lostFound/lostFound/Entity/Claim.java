package com.lostFound.lostFound.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private ItemMatch itemMatch;
    @ManyToOne(optional = false) private User claimant;
    @Column(length = 3000) private String verificationQuestions;
    @Column(length = 5000) private String verificationAnswers;
    private double verificationScore;
    private String status = "VERIFICATION_PENDING";
    private LocalDateTime claimedAt = LocalDateTime.now();
    public Long getId(){return id;}
    public ItemMatch getItemMatch(){return itemMatch;} public void setItemMatch(ItemMatch v){itemMatch=v;}
    public LostItem getLostItem(){return itemMatch == null ? null : itemMatch.getLostItem();}
    public FoundItem getFoundItem(){return itemMatch == null ? null : itemMatch.getFoundItem();}
    public User getClaimant(){return claimant;} public void setClaimant(User v){claimant=v;}
    public String getVerificationQuestions(){return verificationQuestions;} public void setVerificationQuestions(String v){verificationQuestions=v;}
    public String getVerificationAnswers(){return verificationAnswers;} public void setVerificationAnswers(String v){verificationAnswers=v;}
    public String getMessage(){return verificationAnswers;}
    public double getVerificationScore(){return verificationScore;} public void setVerificationScore(double v){verificationScore=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public LocalDateTime getClaimedAt(){return claimedAt;}
}