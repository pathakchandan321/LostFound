package com.lostFound.lostFound.controller;

import com.lostFound.lostFound.Entity.Claim;
import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.service.ChatbotService;
import com.lostFound.lostFound.service.ClaimService;
import com.lostFound.lostFound.service.ItemService;
import com.lostFound.lostFound.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class MainController {

    private final ItemService itemService;
    private final ClaimService claimService;
    private final UserService userService;
    private final ChatbotService chatbotService;

    public MainController(ItemService itemService, ClaimService claimService,
                          UserService userService, ChatbotService chatbotService) {
        this.itemService = itemService;
        this.claimService = claimService;
        this.userService = userService;
        this.chatbotService = chatbotService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String index(Model model) {
        model.addAttribute("chatAnswer", null);
        return "index";
    }

    // Report lost
    @GetMapping("/User/report-lost")
    public String lostForm(Model m) {
        m.addAttribute("lostItem", new LostItem());
        return "User/lost_form";
    }

    @PostMapping("/User/report-lost")
    public String submitLost(@ModelAttribute LostItem lostItem,
            @RequestParam("image") MultipartFile file,
            Authentication authentication) {

        itemService.createLost(lostItem, file, currentUser(authentication));
        return "redirect:/lost-items";
    }

    // Report found (similar)
    @GetMapping("/User/report-found")
    public String foundForm(Model m) {
        m.addAttribute("foundItem", new FoundItem());
        return "User/found_form";
    }

    @PostMapping("/User/report-found")
    public String submitFound(@ModelAttribute FoundItem foundItem,
            @RequestParam("image") MultipartFile file,
            Authentication authentication) {

        itemService.createFound(foundItem, file, currentUser(authentication));
        return "redirect:/found-items";
    }

    @GetMapping("/lost-items")
    public String listLost(Model m, @RequestParam(value = "q", required = false) String q) {
        m.addAttribute("lostItems", itemService.lostItems(q));
        m.addAttribute("query", q);
        return "lost_list";
    }

    @GetMapping("/found-items")
    public String listFound(Model m, @RequestParam(value = "q", required = false) String q) {
        m.addAttribute("foundItems", itemService.foundItems(q));
        m.addAttribute("lostItems", itemService.lostItems(null));
        m.addAttribute("query", q);
        return "found_list";
    }

    // Claim create
    @PostMapping("/claim")
    public String claim(@RequestParam Long lostId, @RequestParam Long foundId,
                        @RequestParam(required = false) String message,
                        Authentication authentication) {
        claimService.create(lostId, foundId, currentUser(authentication), message);
        return "redirect:/claims";
    }

    @GetMapping("/claims")
    public String listClaims(Model m) {
        m.addAttribute("claims", claimService.findAll());
        return "claim_list";
    }

    // Admin approve/reject
    @PostMapping("/admin/claim/{id}/approve")
    public String approve(@PathVariable Long id) {
        claimService.approve(id);
        return "redirect:/claims";
    }

    @PostMapping("/admin/claim/{id}/reject")
    public String reject(@PathVariable Long id) {
        claimService.reject(id);
        return "redirect:/claims";
    }

    @PostMapping("/admin/lost/{id}/delete")
    public String deleteLost(@PathVariable Long id) {
        itemService.deleteLost(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/found/{id}/delete")
    public String deleteFound(@PathVariable Long id) {
        itemService.deleteFound(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/claim/{id}/delete")
    public String deleteClaim(@PathVariable Long id) {
        claimService.delete(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/user/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    // Admin dashboard
    @GetMapping("/admin")
    public String adminDashboard(Model m) {
        m.addAttribute("lost", itemService.lostItems(null));
        m.addAttribute("found", itemService.foundItems(null));
        m.addAttribute("claims", claimService.findAll());
        m.addAttribute("users", userService.findAll());
        return "admin";
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String message, Model model) {
        model.addAttribute("chatAnswer", chatbotService.reply(message));
        return "index";
    }

    private User currentUser(Authentication authentication) {
        String username = authentication == null ? null : authentication.getName();
        return userService.currentOrAnonymous(username);
    }
}
