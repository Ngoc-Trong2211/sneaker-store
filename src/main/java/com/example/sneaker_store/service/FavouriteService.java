package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.response.FavouriteResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface FavouriteService {
    void createFavourite(String guestId, String productId) throws BadRequestException;
    List<FavouriteResponse> getFavouriteById(String guestId);
    void deleteFavourite(String guestId, String productId);
    void mergeGuestFavouriteToUser(String guestId, String userId);
}
