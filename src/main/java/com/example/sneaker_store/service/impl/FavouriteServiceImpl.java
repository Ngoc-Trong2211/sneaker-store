package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.FavouriteEntity;
import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.repository.FavouriteRepository;
import com.example.sneaker_store.repository.ProductRepository;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "FAVOURITE-SERVICE")
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void createFavourite(String guestId, String productId) throws BadRequestException {
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        FavouriteEntity favourite = new FavouriteEntity();
        if (email != null) {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            if (favouriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
                throw new BadRequestException("Sản phẩm đã được yêu thích");
            }
            favourite.setUserId(user.getId());
        } else {
            if (favouriteRepository.existsByGuestIdAndProductId(guestId, productId)) {
                throw new BadRequestException("Sản phẩm đã được yêu thích");
            }
            favourite.setGuestId(guestId);
        }
        favourite.setProductId(productId);
        favouriteRepository.save(favourite);
    }
}
