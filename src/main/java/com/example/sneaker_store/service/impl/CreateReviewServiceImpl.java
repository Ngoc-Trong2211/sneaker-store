package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.review.CreateReviewRequest;
import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.ReviewEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.repository.OrderRepository;
import com.example.sneaker_store.repository.ProductRepository;
import com.example.sneaker_store.repository.ReviewRepository;
import com.example.sneaker_store.service.ReviewService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.exception.RefreshTokenInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.sneaker_store.service.impl.AuthServiceImpl.getCurrentUserLogin;

@Service
@Slf4j(topic = "REVIEW-SERVICE")
@RequiredArgsConstructor
public class CreateReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public void createReview(CreateReviewRequest req) {
        String email = getCurrentUserLogin().isPresent() ? getCurrentUserLogin().get() : "";
        if (email.isEmpty())
            throw new RefreshTokenInvalidException("Email do not match!");
        UserEntity user = this.userService.findByEmail(email);
        ProductEntity product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        boolean hasPurchased = orderRepository.existsCompletedOrder(user.getId(), product.getId());
        if (!hasPurchased) throw new RuntimeException("Bạn cần mua sản phẩm trước khi đánh giá");
        boolean reviewed = reviewRepository.existsByUserIdAndProductId(user.getId() ,product.getId());

        if (reviewed) throw new RuntimeException("Bạn đã đánh giá sản phẩm này");
        ReviewEntity review = new ReviewEntity();

        review.setUserId(user.getId());
        review.setProductId(product.getId());
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        reviewRepository.save(review);
    }
}
