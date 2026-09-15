package com.lostFound.lostFound.controller;

import com.lostFound.lostFound.Entity.*;
import com.lostFound.lostFound.service.ClaimService;
import com.lostFound.lostFound.service.ItemService;
import com.lostFound.lostFound.service.UserService;
import com.lostFound.lostFound.repository.NotificationRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
public class MainController {
    private final ItemService itemService; private final ClaimService claimService; private final UserService userService; private final NotificationRepo notificationRepo;
    public MainController(ItemService itemService, ClaimService claimService, UserService userService, NotificationRepo notificationRepo) { this.itemService=itemService; this.claimService=claimService; this.userService=userService; this.notificationRepo=notificationRepo; }
    @GetMapping("/") public String root(){ return "redirect:/home"; }
    @GetMapping("/home") public String home(Authentication auth, Model model) { if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) return "redirect:/my-reports"; return "index"; }
    @GetMapping("/User/report-lost") public String lostForm(Model model){ model.addAttribute("lostItem",new LostItem()); return "User/lost_form"; }
    @PostMapping("/User/report-lost") public String submitLost(@ModelAttribute LostItem item,@RequestParam("image") MultipartFile image,Authentication auth){ itemService.createLost(item,image,currentUser(auth)); return "redirect:/my-reports"; }
    @GetMapping("/User/report-found") public String foundForm(Model model){ model.addAttribute("foundItem",new FoundItem()); return "User/found_form"; }
    @PostMapping("/User/report-found") public String submitFound(@ModelAttribute FoundItem item,@RequestParam("image") MultipartFile image,Authentication auth){ itemService.createFound(item,image,currentUser(auth)); return "redirect:/my-reports"; }
    @GetMapping("/my-reports") public String dashboard(Authentication auth,Model model){ User user=currentUser(auth); model.addAttribute("lostItems",itemService.lostItemsFor(user)); model.addAttribute("foundItems",itemService.foundItemsFor(user)); model.addAttribute("notifications",notificationRepo.findByRecipientIdOrderByCreatedAtDesc(user.getId())); model.addAttribute("claims",claimService.findFor(user)); return "dashboard"; }
    @GetMapping("/matches/{id}") public String preview(@PathVariable Long id,Authentication auth,Model model){ ItemMatch match=claimService.previewFor(id,currentUser(auth)); model.addAttribute("match",match); return "match_preview"; }
    @PostMapping("/matches/{id}/verify") public String beginVerification(@PathVariable Long id,Authentication auth){ Claim claim=claimService.startVerification(id,currentUser(auth)); return "redirect:/verification/"+claim.getId(); }
    @GetMapping("/verification/{id}") public String verification(@PathVariable Long id,Authentication auth,Model model){ Claim claim=claimService.findFor(currentUser(auth)).stream().filter(c->c.getId().equals(id)).findFirst().orElseThrow(); model.addAttribute("claim",claim); model.addAttribute("questions",claim.getVerificationQuestions().split("\\n")); return "verification"; }
    @PostMapping("/verification/{id}") public String submitVerification(@PathVariable Long id,@RequestParam String answer1,@RequestParam String answer2,@RequestParam String answer3,Authentication auth){ claimService.submitVerification(id,currentUser(auth),List.of(answer1,answer2,answer3)); return "redirect:/my-reports"; }
    @GetMapping("/admin") public String admin(Model model){ model.addAttribute("claims",claimService.pendingAdminReview()); return "admin"; }
    @PostMapping("/admin/claim/{id}/approve") public String approve(@PathVariable Long id){ claimService.approve(id); return "redirect:/admin"; }
    @PostMapping("/admin/claim/{id}/reject") public String reject(@PathVariable Long id){ claimService.reject(id); return "redirect:/admin"; }
    private User currentUser(Authentication auth){ return userService.currentOrAnonymous(auth == null ? null : auth.getName()); }
}