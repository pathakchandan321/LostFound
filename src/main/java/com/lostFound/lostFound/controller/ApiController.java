package com.lostFound.lostFound.controller;

import com.lostFound.lostFound.Entity.*;
import com.lostFound.lostFound.dto.*;
import com.lostFound.lostFound.dto.AuthDtos.RegisterRequest;
import com.lostFound.lostFound.dto.AuthDtos.UserResponse;
import com.lostFound.lostFound.dto.ChatDtos.*;
import com.lostFound.lostFound.dto.ItemDtos.*;
import com.lostFound.lostFound.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController @RequestMapping("/api")
public class ApiController {
    private final UserService users; private final ItemService items; private final ClaimService claims; private final ChatbotService chatbot;
    public ApiController(UserService users,ItemService items,ClaimService claims,ChatbotService chatbot){this.users=users;this.items=items;this.claims=claims;this.chatbot=chatbot;}
    @PostMapping("/auth/register") @ResponseStatus(HttpStatus.CREATED) public UserResponse register(@Valid @RequestBody RegisterRequest request){return user(users.register(request));}
    @GetMapping("/lost-items") public List<LostItemResponse> lost(Authentication auth){return items.lostItemsFor(current(auth)).stream().map(LostItemResponse::from).toList();}
    @GetMapping("/found-items") public List<FoundItemResponse> found(Authentication auth){return items.foundItemsFor(current(auth)).stream().map(FoundItemResponse::from).toList();}
    @PostMapping(value="/lost-items",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) @ResponseStatus(HttpStatus.CREATED) public LostItemResponse createLost(@Valid @ModelAttribute ItemRequest request,@RequestPart MultipartFile image,Authentication auth){LostItem item=new LostItem();item.setItemName(request.itemName());item.setDescription(request.description());item.setCategory(request.category());item.setLocation(request.location());return LostItemResponse.from(items.createLost(item,image,current(auth)));}
    @PostMapping(value="/found-items",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) @ResponseStatus(HttpStatus.CREATED) public FoundItemResponse createFound(@Valid @ModelAttribute FoundItemRequest request,@RequestPart MultipartFile image,Authentication auth){FoundItem item=new FoundItem();item.setItemName(request.itemName());item.setDescription(request.description());item.setCategory(request.category());item.setLocation(request.location());item.setPrivateDetails(request.privateDetails());return FoundItemResponse.from(items.createFound(item,image,current(auth)));}
    @PostMapping("/matches/{id}/verify") @ResponseStatus(HttpStatus.CREATED) public Claim start(@PathVariable Long id,Authentication auth){return claims.startVerification(id,current(auth));}
    @PostMapping("/chatbot") public ChatResponse chatbot(@Valid @RequestBody ChatRequest request){return new ChatResponse(chatbot.reply(request.message()));}
    private User current(Authentication auth){return users.currentOrAnonymous(auth == null ? null : auth.getName());} private UserResponse user(User value){return new UserResponse(value.getId(),value.getUsername(),value.getEmail(),value.getPhone(),value.getRole());}
}