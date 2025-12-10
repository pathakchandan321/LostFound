package com.lostFound.lostFound.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class FoundItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemName;
    @Column(length=1000) private String description;
    private String location;
    private LocalDate dateFound;
    private String imagePath;
    private String status = "PENDING"; // PENDING, MATCHED, RETURNED

    @ManyToOne
    private User reporter;

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

    public LocalDate getDateFound() {
        return dateFound;
    }

    public void setDateFound(LocalDate dateFound) {
        this.dateFound = dateFound;
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

