package com.lostFound.lostFound.controller;

import com.lostFound.lostFound.Entity.FoundItem;
import com.lostFound.lostFound.Entity.LostItem;
import com.lostFound.lostFound.Entity.User;
import com.lostFound.lostFound.dto.AuthDtos.RegisterRequest;
import com.lostFound.lostFound.dto.AuthDtos.UserResponse;
import com.lostFound.lostFound.dto.ChatDtos.ChatRequest;
import com.lostFound.lostFound.dto.ChatDtos.ChatResponse;
import com.lostFound.lostFound.dto.ClaimDtos.ClaimRequest;
import com.lostFound.lostFound.dto.ClaimDtos.ClaimResponse;
import com.lostFound.lostFound.dto.ItemDtos.FoundItemResponse;
import com.lostFound.lostFound.dto.ItemDtos.ItemRequest;
import com.lostFound.lostFound.dto.ItemDtos.LostItemResponse;
import com.lostFound.lostFound.service.ChatbotService;
import com.lostFound.lostFound.service.ClaimService;
import com.lostFound.lostFound.service.ItemService;
import com.lostFound.lostFound.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserService userService;
    private final ItemService itemService;
    private final ClaimService claimService;
    private final ChatbotService chatbotService;

    public ApiController(UserService userService, ItemService itemService,
                         ClaimService claimService, ChatbotService chatbotService) {
        this.userService = userService;
        this.itemService = itemService;
        this.claimService = claimService;
        this.chatbotService = chatbotService;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return toUser(userService.register(request));
    }

    @GetMapping("/lost-items")
    public List<LostItemResponse> lostItems(@RequestParam(required = false) String q) {
        return itemService.lostItems(q).stream().map(LostItemResponse::from).toList();
    }

    @PostMapping(value = "/lost-items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public LostItemResponse createLost(@Valid @ModelAttribute ItemRequest request,
                                       @RequestPart(required = false) MultipartFile image,
                                       Authentication authentication) {
        LostItem item = new LostItem();
        item.setItemName(request.itemName());
        item.setDescription(request.description());
        item.setLocation(request.location());
        return LostItemResponse.from(itemService.createLost(item, image, currentUser(authentication)));
    }

    @GetMapping("/found-items")
    public List<FoundItemResponse> foundItems(@RequestParam(required = false) String q) {
        return itemService.foundItems(q).stream().map(FoundItemResponse::from).toList();
    }

    @PostMapping(value = "/found-items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FoundItemResponse createFound(@Valid @ModelAttribute ItemRequest request,
                                         @RequestPart(required = false) MultipartFile image,
                                         Authentication authentication) {
        FoundItem item = new FoundItem();
        item.setItemName(request.itemName());
        item.setDescription(request.description());
        item.setLocation(request.location());
        return FoundItemResponse.from(itemService.createFound(item, image, currentUser(authentication)));
    }

    @GetMapping("/claims")
    public List<ClaimResponse> claims() {
        return claimService.findAll().stream().map(ClaimResponse::from).toList();
    }

    @PostMapping("/claims")
    @ResponseStatus(HttpStatus.CREATED)
    public ClaimResponse createClaim(@Valid @RequestBody ClaimRequest request, Authentication authentication) {
        return ClaimResponse.from(claimService.create(
                request.lostItemId(),
                request.foundItemId(),
                currentUser(authentication),
                request.message()));
    }

    @PostMapping("/admin/claims/{id}/approve")
    public ClaimResponse approve(@PathVariable Long id) {
        return ClaimResponse.from(claimService.approve(id));
    }

    @PostMapping("/admin/claims/{id}/reject")
    public ClaimResponse reject(@PathVariable Long id) {
        return ClaimResponse.from(claimService.reject(id));
    }

    @GetMapping("/admin/users")
    public List<UserResponse> users() {
        return userService.findAll().stream().map(this::toUser).toList();
    }

    @PostMapping("/chatbot")
    public ChatResponse chatbot(@Valid @RequestBody ChatRequest request) {
        return new ChatResponse(chatbotService.reply(request.message()));
    }

    private User currentUser(Authentication authentication) {
        String username = authentication == null ? null : authentication.getName();
        return userService.currentOrAnonymous(username);
    }

    private UserResponse toUser(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getRole());
    }
}
