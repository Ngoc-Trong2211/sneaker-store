package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.response.FavouriteResponse;
import com.example.sneaker_store.model.FavouriteEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.repository.FavouriteRepository;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j(topic = "FAVOURITE-SERVICE")
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    @PreAuthorize("hasAuthority('FAVOURITE_CREATE') or isAnonymous() or hasAuthority('USER')")
    public void createFavourite(String guestId, String productId) throws BadRequestException {
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        FavouriteEntity favourite = new FavouriteEntity();
        if (email != null && !email.equals("anonymousUser")) {
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

    @Override
    @PreAuthorize("hasAuthority('FAVOURITE_READ') or isAnonymous() or hasAuthority('USER')")
    public List<FavouriteResponse> getFavouriteById(String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        String sql = String.join(" ",
                "SELECT",
                "p.id AS product_id,",
                "p.name AS product_name,",
                "p.price AS price,",
                "pv.color AS color,",
                "pi.imageurl AS image_url,",
                "p.slug AS slug",
                "FROM tbl_favourite f",
                "JOIN tbl_product p ON f.product_id = p.id",
                "JOIN tbl_product_variant pv ON pv.product_id = p.id",
                "JOIN tbl_product_image pi ON pi.variant_id = pv.id",
                "WHERE pi.is_main = true"
        );
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (email != null && !email.equals("anonymousUser")) {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            sql += " AND f.user_id = :userId";
            params.addValue("userId", user.getId());
        } else {
            sql += " AND f.guest_id = :guestId";
            params.addValue("guestId", guestId);
        }
        Map<String, FavouriteResponse> map = new LinkedHashMap<>();

        namedParameterJdbcTemplate.query(sql, params, rs -> {
            String productId = rs.getString("product_id");
            FavouriteResponse response = map.get(productId);

            if (response == null) {
                response = new FavouriteResponse();
                response.setProductId(productId);
                response.setProductName(rs.getString("product_name"));
                response.setPrice(rs.getDouble("price"));
                response.setVariants(new ArrayList<>());
                response.setSlug(rs.getString("slug"));
                map.put(productId, response);
            }

            response.getVariants().add(new FavouriteResponse.Variant(rs.getString("color"),
                            rs.getString("image_url")));
        });
        return new ArrayList<>(map.values());
    }

    @Override
    @Transactional
    public void mergeGuestFavouriteToUser(String guestId, String userId) {
        List<FavouriteEntity> guestFavourites = favouriteRepository.findByGuestId(guestId);
        for (FavouriteEntity guestFav : guestFavourites) {
            if (!favouriteRepository.existsByUserIdAndProductId(userId, guestFav.getProductId())) {
                FavouriteEntity newFav = new FavouriteEntity();
                newFav.setUserId(userId);
                newFav.setProductId(guestFav.getProductId());
                favouriteRepository.save(newFav);
            }
        }
        favouriteRepository.deleteByGuestId(guestId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('FAVOURITE_DELETE') or isAnonymous() or hasAuthority('USER')")
    public void deleteFavourite(String guestId, String productId) {
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        if (email != null && !email.equals("anonymousUser")) {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            if (!favouriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
                throw new RuntimeException("Sản phẩm không tồn tại trong danh sách yêu thích");
            }
            favouriteRepository.deleteByUserIdAndProductId(user.getId(), productId);
        } else {
            if (!favouriteRepository.existsByGuestIdAndProductId(guestId, productId)) {
                throw new RuntimeException("Sản phẩm không tồn tại trong danh sách yêu thích");
            }
            favouriteRepository.deleteByGuestIdAndProductId(guestId, productId);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteFavouriteGuest() {
        Instant expiredDate = Instant.now().minus(30, ChronoUnit.DAYS);
        favouriteRepository.deleteExpiredGuestFavourite(expiredDate);
    }
}
