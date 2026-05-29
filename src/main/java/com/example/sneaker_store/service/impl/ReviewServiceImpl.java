package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.review.CreateReviewRequest;
import com.example.sneaker_store.dto.response.review.GetReviewResponse;
import com.example.sneaker_store.model.OrderEntity;
import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.ReviewEligibilityEntity;
import com.example.sneaker_store.model.ReviewEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.repository.OrderRepository;
import com.example.sneaker_store.repository.ProductRepository;
import com.example.sneaker_store.repository.ReviewEligibilityRepository;
import com.example.sneaker_store.repository.ReviewRepository;
import com.example.sneaker_store.service.ReviewService;
import com.example.sneaker_store.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.sneaker_store.service.impl.AuthServiceImpl.getCurrentUserLogin;

@Service
@Slf4j(topic = "REVIEW-SERVICE")
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewEligibilityRepository reviewEligibilityRepository;

    private void updateProductRating(ProductEntity product) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
        product.setRating(avgRating);
        productRepository.save(product);
    }

    @Override
    public boolean canReviewByOrderCodeAndProduct(String codeOrder, String productId) {
        if (!hasText(codeOrder) || !hasText(productId)) {
            return false;
        }
        return orderRepository.findByCodeAndProductId(codeOrder, productId)
                .map(order -> reviewEligibilityRepository.existsByOrderIdAndProductIdAndStatusFalse(
                        order.getId(),
                        productId
                ))
                .orElse(false);
    }

    @Override
    public GetReviewResponse getReviewsByProduct(String productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        GetReviewResponse response = new GetReviewResponse();
        response.setProductId(product.getId());
        response.setAverageRating(reviewRepository.getAverageRatingByProductId(product.getId()));
        response.setTotalReviews(reviewRepository.countByProductId(product.getId()));
        response.setReviews(reviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId())
                .stream()
                .map(this::toReviewResponse)
                .toList());
        return response;
    }

    @Override
    public List<GetReviewResponse.Review> getReviewsByUserId(String userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toReviewResponse)
                .toList();
    }

    @Override
    @Transactional
    public void createReview(CreateReviewRequest req) {
        ProductEntity product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        UserEntity currentUser = getCurrentUser();
        ReviewEligibilityEntity eligibility = getEligibility(req, product.getId(), currentUser);

        OrderEntity order = orderRepository.findById(eligibility.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ReviewEntity review = new ReviewEntity();
        review.setUserId(eligibility.getUserId());
        review.setProductId(product.getId());
        review.setStar(req.getStar());
        review.setRating(req.getStar());
        review.setComment(req.getComment());
        review.setPhone(currentUser != null && currentUser.getId().equals(eligibility.getUserId())
                ? currentUser.getPhone()
                : order.getPhone());
        review.setOrderCode(order.getCode());
        reviewRepository.save(review);

        eligibility.setReviewId(review.getId());
        eligibility.setStatus(true);
        reviewEligibilityRepository.save(eligibility);

        updateProductRating(product);
    }

    private ReviewEligibilityEntity getEligibility(
            CreateReviewRequest req,
            String productId,
            UserEntity currentUser
    ) {
        if (hasText(req.getOrderItemId())) {
            return getEligibilityByOrderItemId(req.getOrderItemId(), req.getCodeOrder(), productId, currentUser);
        }
        if (hasText(req.getCodeOrder())) {
            return getEligibilityByOrderCode(req.getCodeOrder(), productId);
        }
        return getEligibilityByCurrentUser(currentUser, productId);
    }

    private ReviewEligibilityEntity getEligibilityByOrderItemId(
            String orderItemId,
            String codeOrder,
            String productId,
            UserEntity currentUser
    ) {
        ReviewEligibilityEntity eligibility = reviewEligibilityRepository.findByOrderItemIdAndStatusFalse(orderItemId)
                .orElseThrow(() -> new RuntimeException("This order item has no available review"));
        if (!productId.equals(eligibility.getProductId())) {
            throw new RuntimeException("Product does not match this order item");
        }
        OrderEntity order = orderRepository.findById(eligibility.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (hasText(codeOrder) && !codeOrder.equals(order.getCode())) {
            throw new RuntimeException("Order code does not match this order item");
        }
        if (!hasText(codeOrder)) {
            if (currentUser == null) {
                throw new RuntimeException("codeOrder is required for guest review");
            }
            if (eligibility.getUserId() == null || !eligibility.getUserId().equals(currentUser.getId())) {
                throw new RuntimeException("This order item does not belong to current user");
            }
        }
        return eligibility;
    }

    private ReviewEligibilityEntity getEligibilityByOrderCode(String codeOrder, String productId) {
        OrderEntity order = orderRepository.findByCodeAndProductId(codeOrder, productId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return reviewEligibilityRepository.findFirstByOrderIdAndProductIdAndStatusFalseOrderByCreatedAtAsc(
                order.getId(),
                productId
        ).orElseThrow(() -> new RuntimeException("This order has no available review"));
    }

    private ReviewEligibilityEntity getEligibilityByCurrentUser(UserEntity user, String productId) {
        if (user == null) {
            throw new RuntimeException("codeOrder is required for guest review");
        }
        return reviewEligibilityRepository.findFirstByUserIdAndProductIdAndStatusFalseOrderByCreatedAtAsc(
                user.getId(),
                productId
        ).orElseThrow(() -> new RuntimeException("You have no available review for this product"));
    }

    private UserEntity getCurrentUser() {
        Optional<String> email = getCurrentUserLogin()
                .filter(value -> !"anonymousUser".equals(value));
        return email.map(userService::findByEmail).orElse(null);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private GetReviewResponse.Review toReviewResponse(ReviewEntity review) {
        GetReviewResponse.Review response = new GetReviewResponse.Review();
        response.setId(review.getId());
        response.setProductId(review.getProductId());
        response.setUserId(review.getUserId());
        response.setPhone(review.getPhone());
        response.setOrderCode(review.getOrderCode());
        response.setStar(review.getStar());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}
