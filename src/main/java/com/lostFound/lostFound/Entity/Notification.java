package com.lostFound.lostFound.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private User recipient;
    @ManyToOne(optional = false) private ItemMatch itemMatch;
    @Column(length = 500) private String message;
    private boolean read;
    private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId(){return id;} public User getRecipient(){return recipient;} public void setRecipient(User v){recipient=v;}
    public ItemMatch getItemMatch(){return itemMatch;} public void setItemMatch(ItemMatch v){itemMatch=v;}
    public String getMessage(){return message;} public void setMessage(String v){message=v;}
    public boolean isRead(){return read;} public void setRead(boolean v){read=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}