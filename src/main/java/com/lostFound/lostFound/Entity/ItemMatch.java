package com.lostFound.lostFound.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"lost_item_id", "found_item_id"}))
public class ItemMatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private LostItem lostItem;
    @ManyToOne(optional = false) private FoundItem foundItem;
    private double confidenceScore;
    private double imageScore;
    private double keywordScore;
    private double semanticScore;
    private double categoryScore;
    private double locationScore;
    private double timeScore;
    private String status = "SUGGESTED";
    private LocalDateTime createdAt = LocalDateTime.now();
    @Version private Long version;
    public Long getId(){return id;} public LostItem getLostItem(){return lostItem;} public void setLostItem(LostItem v){lostItem=v;}
    public FoundItem getFoundItem(){return foundItem;} public void setFoundItem(FoundItem v){foundItem=v;}
    public double getConfidenceScore(){return confidenceScore;} public void setConfidenceScore(double v){confidenceScore=v;}
    public double getImageScore(){return imageScore;} public void setImageScore(double v){imageScore=v;}
    public double getKeywordScore(){return keywordScore;} public void setKeywordScore(double v){keywordScore=v;}
    public double getSemanticScore(){return semanticScore;} public void setSemanticScore(double v){semanticScore=v;}
    public double getCategoryScore(){return categoryScore;} public void setCategoryScore(double v){categoryScore=v;}
    public double getLocationScore(){return locationScore;} public void setLocationScore(double v){locationScore=v;}
    public double getTimeScore(){return timeScore;} public void setTimeScore(double v){timeScore=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}