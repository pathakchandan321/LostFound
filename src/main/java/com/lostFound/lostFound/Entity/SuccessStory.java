package com.lostFound.lostFound.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SuccessStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private Claim claim;

    @ManyToOne(optional = false)
    private User author;

    @Column(nullable = false, length = 2000)
    private String story;

    private String imagePath;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public String getStory() { return story; }
    public void setStory(String story) { this.story = story; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
