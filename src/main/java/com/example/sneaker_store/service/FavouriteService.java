package com.example.sneaker_store.service;

import org.apache.coyote.BadRequestException;

public interface FavouriteService {
    void createFavourite(String guestId, String productId) throws BadRequestException;
}
