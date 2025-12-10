package com.lostFound.lostFound.controller;

import com.lostFound.lostFound.Entity.Claim;
import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.repository.ClaimRepo;
import com.lostFound.lostFound.repository.FoundItemRepo;
import com.lostFound.lostFound.repository.LostItemRepo;
import com.lostFound.lostFound.repository.UserRepo;
import com.lostFound.lostFound.service.MatchingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class MainController {

    private final LostItemRepo lostRepo;
    private final FoundItemRepo foundRepo;
    private final UserRepo userRepo;
    private final ClaimRepo claimRepo;
    private final MatchingService matchingService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public MainController(LostItemRepo lostRepo, FoundItemRepo foundRepo, UserRepo userRepo,
                          ClaimRepo claimRepo, MatchingService matchingService) {
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
        this.userRepo = userRepo;
        this.claimRepo = claimRepo;
        this.matchingService = matchingService;
    }

    @GetMapping("/home")
    public String index() { return "index"; }

    // Report lost
    @GetMapping("/report-lost")
    public String lostForm(Model m){
        m.addAttribute("lostItem", new LostItem());
        return "lost_form";
    }

    @PostMapping("/report-lost")
    public String submitLost(@ModelAttribute LostItem lostItem,
                             @RequestParam("image") MultipartFile file,
                             @RequestParam(value="reporterName", required=false) String reporterName) throws IOException {

        // create user
        User u = new User();
        u.setName((reporterName == null || reporterName.isBlank()) ? "Anonymous" : reporterName);
        userRepo.save(u);

        lostItem.setReporter(u);
        lostItem.setDateLost(LocalDate.now());

        // IMAGE UPLOAD FIXED
        if (!file.isEmpty()) {

            // ALWAYS USE ABSOLUTE PATH
            String uploadPath = System.getProperty("user.dir") + "/uploads/";
            File uploadFolder = new File(uploadPath);

            // create folder if missing
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // unique file name
            String fileName = System.currentTimeMillis() + "_"
                    + StringUtils.cleanPath(file.getOriginalFilename());

            // full file path
            File savedFile = new File(uploadPath + fileName);

            // save file
            file.transferTo(savedFile);

            // store file name in database
            lostItem.setImagePath(fileName);
        }

        // save lost item
        lostRepo.save(lostItem);

        // matching logic
        List<FoundItem> matches = matchingService.findMatchesForLost(lostItem);
        if (!matches.isEmpty()) {
            lostItem.setStatus("MATCHED");
            lostRepo.save(lostItem);
        }

        return "redirect:/lost-items";
    }


    // Report found (similar)
    @GetMapping("/report-found")
    public String foundForm(Model m){
        m.addAttribute("foundItem", new FoundItem());
        return "found_form";
    }

    @PostMapping("/report-found")
    public String submitFound(@ModelAttribute FoundItem foundItem,
                              @RequestParam("image") MultipartFile file,
                              @RequestParam(value = "reporterName", required = false) String reporterName) throws IOException {

        // Create user
        User u = new User();
        u.setName((reporterName == null || reporterName.isBlank()) ? "Anonymous" : reporterName);
        userRepo.save(u);

        foundItem.setReporter(u);
        foundItem.setDateFound(LocalDate.now());

        // ---------- FIXED IMAGE UPLOAD (same as report-lost) ----------
        if (!file.isEmpty()) {

            // ALWAYS use absolute path (no Tomcat temp folder)
            String uploadPath = System.getProperty("user.dir") + "/uploads/";
            File uploadFolder = new File(uploadPath);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();

            // Unique file name
            String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

            // Save the file
            File savedFile = new File(uploadPath + fileName);
            file.transferTo(savedFile);

            foundItem.setImagePath(fileName);
        }
        // ---------------------------------------------------------------

        foundRepo.save(foundItem);

        // matching logic
        List<LostItem> matches = matchingService.findMatchesForFound(foundItem);
        if (!matches.isEmpty()) {
            foundItem.setStatus("MATCHED");
            foundRepo.save(foundItem);
        }

        return "redirect:/found-items";
    }


    @GetMapping("/lost-items")
    public String listLost(Model m, @RequestParam(value="q", required=false) String q) {
        List<LostItem> list = (q==null||q.isBlank()) ? lostRepo.findAll() : lostRepo.findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(q,q);
        m.addAttribute("lostItems", list);
        return "lost_list";
    }

    @GetMapping("/found-items")
    public String listFound(Model m, @RequestParam(value="q", required=false) String q) {
        List<FoundItem> list = (q==null||q.isBlank()) ? foundRepo.findAll() : foundRepo.findByItemNameContainingIgnoreCaseOrLocationContainingIgnoreCase(q,q);
        m.addAttribute("foundItems", list);
        return "found_list";
    }

    // Claim create
    @PostMapping("/claim")
    public String claim(@RequestParam Long lostId, @RequestParam Long foundId, @RequestParam String claimantName) {
        LostItem lost = lostRepo.findById(lostId).orElse(null);
        FoundItem found = foundRepo.findById(foundId).orElse(null);
        if (lost == null || found == null) return "redirect:/";

        User u = new User();
        u.setName(claimantName==null||claimantName.isBlank()? "Anonymous": claimantName);
        userRepo.save(u);

        Claim c = new Claim();
        c.setLostItem(lost);
        c.setFoundItem(found);
        c.setClaimant(u);
        claimRepo.save(c);
        return "redirect:/claims";
    }

    @GetMapping("/claims")
    public String listClaims(Model m) {
        m.addAttribute("claims", claimRepo.findAll());
        return "claim_list";
    }

    // Admin approve/reject
    @PostMapping("/admin/claim/{id}/approve")
    public String approve(@PathVariable Long id) {
        Claim c = claimRepo.findById(id).orElse(null);
        if (c!=null) {
            c.setStatus("APPROVED");
            claimRepo.save(c);
            LostItem l = c.getLostItem(); FoundItem f = c.getFoundItem();
            l.setStatus("RETURNED"); f.setStatus("RETURNED");
            lostRepo.save(l); foundRepo.save(f);
        }
        return "redirect:/claims";
    }
    @PostMapping("/admin/claim/{id}/reject")
    public String reject(@PathVariable Long id) {
        Claim c = claimRepo.findById(id).orElse(null);
        if (c!=null) {
            c.setStatus("REJECTED");
            claimRepo.save(c);
        }
        return "redirect:/claims";
    }

    // Admin dashboard
    @GetMapping("/admin")
    public String adminDashboard(Model m) {
        m.addAttribute("lost", lostRepo.findAll());
        m.addAttribute("found", foundRepo.findAll());
        m.addAttribute("claims", claimRepo.findAll());
        return "admin";
    }
}

