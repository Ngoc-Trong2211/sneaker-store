package com.example.sneaker_store.controller;

import com.example.sneaker_store.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "FAVOURITE-CONTROLLER")
@RequestMapping("/favourite/v1")
public class FavouriteController {
    private final FavouriteService favouriteService;

    @PostMapping("/favourites")
    public ResponseEntity<Void> createFavourite(@CookieValue(value = "guest_id", required = false) String guestId,
                                                @RequestParam String productId) throws BadRequestException {
        favouriteService.createFavourite(guestId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
