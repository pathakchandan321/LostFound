package com.lostFound.lostFound.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class FoundItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String itemName;
    @NotBlank @Column(length = 1000) private String description;
    @NotBlank private String category;
    @NotBlank private String location;
    @NotBlank @Column(length = 1500) private String privateDetails;
    private LocalDate dateFound;
    private LocalDateTime occurredAt;
    private String imagePath;
    @Column(length = 128) private String imageHash;
    private String status = "FOUND";
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne private User reporter;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getItemName(){return itemName;} public void setItemName(String value){itemName=value;}
    public String getDescription(){return description;} public void setDescription(String value){description=value;}
    public String getCategory(){return category;} public void setCategory(String value){category=value;}
    public String getLocation(){return location;} public void setLocation(String value){location=value;}
    public String getPrivateDetails(){return privateDetails;} public void setPrivateDetails(String value){privateDetails=value;}
    public LocalDate getDateFound(){return dateFound;} public void setDateFound(LocalDate value){dateFound=value;}
    public LocalDateTime getOccurredAt(){return occurredAt;} public void setOccurredAt(LocalDateTime value){occurredAt=value;}
    public String getImagePath(){return imagePath;} public void setImagePath(String value){imagePath=value;}
    public String getImageHash(){return imageHash;} public void setImageHash(String value){imageHash=value;}
    public String getStatus(){return status;} public void setStatus(String value){status=value;}
    public boolean isActive(){return active;} public void setActive(boolean value){active=value;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime value){createdAt=value;}
    public User getReporter(){return reporter;} public void setReporter(User value){reporter=value;}
}