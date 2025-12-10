package com.lostFound.lostFound.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class LostItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemName;
    @Column(length=1000) private String description;
    private String location;
    private LocalDate dateLost;
    private String imagePath;
    private String status = "PENDING"; // PENDING, MATCHED, RETURNED

    @ManyToOne
    private User reporter;

    public LostItem() {

    }

    public LostItem(Long id, String itemName, String description, String location, LocalDate dateLost, String imagePath, String status, User reporter) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.location = location;
        this.dateLost = dateLost;
        this.imagePath = imagePath;
        this.status = status;
        this.reporter = reporter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDateLost() {
        return dateLost;
    }

    public void setDateLost(LocalDate dateLost) {
        this.dateLost = dateLost;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }
    // getters & setters
}

