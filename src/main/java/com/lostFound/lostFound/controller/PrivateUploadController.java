package com.lostFound.lostFound.controller;

import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.exception.ResourceNotFoundException;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import com.lostFound.lostFound.service.FileStorageService;
import com.lostFound.lostFound.service.UserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;

@RestController
public class PrivateUploadController {
 private final LostItemRepo lost; private final FoundItemRepo found; private final FileStorageService storage; private final UserService users;
 public PrivateUploadController(LostItemRepo lost,FoundItemRepo found,FileStorageService storage,UserService users){this.lost=lost;this.found=found;this.storage=storage;this.users=users;}
 @GetMapping(value="/uploads/{fileName:.+}") public Resource image(@PathVariable String fileName, Authentication auth){
  User user=users.currentOrAnonymous(auth.getName()); boolean admin="ADMIN".equals(user.getRole());
  boolean owns=lost.findByImagePath(fileName).map(item->item.getReporter().getId().equals(user.getId())).orElse(false) || found.findByImagePath(fileName).map(item->item.getReporter().getId().equals(user.getId())).orElse(false);
  if(!admin && !owns) throw new ResourceNotFoundException("Image not found"); Path path=storage.uploadPath().resolve(fileName).normalize(); if(!path.startsWith(storage.uploadPath())) throw new ResourceNotFoundException("Image not found"); return new FileSystemResource(path);
 }
}