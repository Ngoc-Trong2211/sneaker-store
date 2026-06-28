package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.review.CreateReviewRequest;
import com.example.sneaker_store.dto.request.review.SpecificationReviewRequest;
import com.example.sneaker_store.dto.response.review.GetReviewPageResponse;
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
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.ReviewService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.specification.ReviewSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private static final Set<String> INVALID_REVIEW_WORDS = Set.of(
            "dit",
            "địt",
            "dm",
            "đm",
            "dmm",
            "đmm",
            "clm",
            "vcl",
            "vl",
            "lon",
            "lồn",
            "cac",
            "cặc",
            "du",
            "đụ",
            "deo",
            "đéo",
            "fuck",
            "shit",
            "bitch"
    );
    private static final Pattern INVALID_REVIEW_WORD_PATTERN = Pattern.compile(
            "(?<![\\p{L}\\p{N}_])("
                    + String.join("|", INVALID_REVIEW_WORDS.stream().map(Pattern::quote).toList())
                    + ")(?![\\p{L}\\p{N}_])",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS
    );

    private void updateProductRating(ProductEntity product) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
        product.setRating(avgRating);
        productRepository.save(product);
    }

    @Override
    @PreAuthorize("hasAuthority('REVIEW_READ') or isAnonymous() or hasAuthority('USER')")
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
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

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
    @PreAuthorize("hasAuthority('REVIEW_READ_USER') or hasAuthority('USER')")
    public List<GetReviewResponse.Review> getReviewsByUserId(String userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toReviewResponse)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('REVIEW_CREATE') or isAnonymous() or hasAuthority('USER')")
    public void createReview(CreateReviewRequest req) {
        ProductEntity product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        validateReviewComment(req.getComment());

        UserEntity currentUser = getCurrentUser();
        ReviewEligibilityEntity eligibility = getEligibility(req, product.getId(), currentUser);

        OrderEntity order = orderRepository.findById(eligibility.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

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

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('REVIEW_DELETE') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public void deleteReview(Long id) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đánh giá không tồn tại"));
        ProductEntity product = productRepository.findById(review.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        reviewEligibilityRepository.findByReviewId(review.getId()).ifPresent(eligibility -> {
            eligibility.setReviewId(null);
            eligibility.setStatus(false);
            reviewEligibilityRepository.save(eligibility);
        });

        reviewRepository.deleteById(review.getId());
        updateProductRating(product);
    }

    private void validateReviewComment(String comment) {
        List<String> invalidWords = findInvalidReviewWords(comment);
        if (!invalidWords.isEmpty()) {
            throw new RuntimeException("Nội dung đánh giá chứa từ không hợp lệ: " + String.join(", ", invalidWords));
        }
    }

    private List<String> findInvalidReviewWords(String comment) {
        if (!hasText(comment)) {
            return List.of();
        }

        Matcher matcher = INVALID_REVIEW_WORD_PATTERN.matcher(comment);
        Set<String> invalidWords = new LinkedHashSet<>();
        while (matcher.find()) {
            invalidWords.add(matcher.group());
        }
        return List.copyOf(invalidWords);
    }

    private ReviewEligibilityEntity getEligibility(
            CreateReviewRequest req,
            String productId,
            UserEntity currentUser
    ) {
        if (hasText(req.getOrderItemId())) {
            return getEligibilityByOrderItemId(req.getOrderItemId(), req.getOrderCode(), productId, currentUser);
        }
        if (hasText(req.getOrderCode())) {
            return getEligibilityByOrderCode(req.getOrderCode(), productId);
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
                .orElseThrow(() -> new RuntimeException("Chi tiết đơn hàng này không còn lượt đánh giá"));
        if (!productId.equals(eligibility.getProductId())) {
            throw new RuntimeException("Sản phẩm không khớp với chi tiết đơn hàng");
        }
        OrderEntity order = orderRepository.findById(eligibility.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (hasText(codeOrder) && !codeOrder.equals(order.getCode())) {
            throw new RuntimeException("Mã đơn hàng không khớp với chi tiết đơn hàng");
        }
        if (!hasText(codeOrder)) {
            if (currentUser == null) {
                throw new RuntimeException("Mã đơn hàng là bắt buộc khi khách vãng lai đánh giá");
            }
            if (eligibility.getUserId() == null || !eligibility.getUserId().equals(currentUser.getId())) {
                throw new RuntimeException("Chi tiết đơn hàng không thuộc về người dùng hiện tại");
            }
        }
        return eligibility;
    }

    private ReviewEligibilityEntity getEligibilityByOrderCode(String codeOrder, String productId) {
        OrderEntity order = orderRepository.findByCodeAndProductId(codeOrder, productId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        return reviewEligibilityRepository.findFirstByOrderIdAndProductIdAndStatusFalseOrderByCreatedAtAsc(
                order.getId(),
                productId
        ).orElseThrow(() -> new RuntimeException("Đơn hàng này không còn lượt đánh giá"));
    }

    private ReviewEligibilityEntity getEligibilityByCurrentUser(UserEntity user, String productId) {
        if (user == null) {
            throw new RuntimeException("Mã đơn hàng là bắt buộc khi khách vãng lai đánh giá");
        }
        return reviewEligibilityRepository.findFirstByUserIdAndProductIdAndStatusFalseOrderByCreatedAtAsc(
                user.getId(),
                productId
        ).orElseThrow(() -> new RuntimeException("Bạn không còn lượt đánh giá cho sản phẩm này"));
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
        response.setUserName(resolveReviewUserName(review));
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

    private String resolveReviewUserName(ReviewEntity review) {
        if (hasText(review.getUserId())) {
            return userRepository.findById(review.getUserId())
                    .map(UserEntity::getName)
                    .orElse(null);
        }
        if (hasText(review.getOrderCode())) {
            return orderRepository.findByCode(review.getOrderCode())
                    .map(OrderEntity::getReceiverName)
                    .orElse(null);
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAuthority('REVIEW_READ') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public GetReviewPageResponse getReview(Pageable pageable, SpecificationReviewRequest req) {
        Specification<ReviewEntity> spec = ReviewSpecification.specReview(req);
        Page<ReviewEntity> page = this.reviewRepository.findAll(spec, pageable);
        GetReviewPageResponse res = new GetReviewPageResponse();
        GetReviewPageResponse.DataPage pageRes = this.modelMapper.map(page, GetReviewPageResponse.DataPage.class);
        res.setPage(pageRes);
        List<GetReviewPageResponse.Review> reviews = page.getContent().stream().map(this::toReviewPageResponse).toList();
        res.setReviews(reviews);
        return res;
    }

    private GetReviewPageResponse.Review toReviewPageResponse(ReviewEntity review) {
        GetReviewPageResponse.Review response = new GetReviewPageResponse.Review();
        response.setId(review.getId());
        response.setProductName(resolveProductName(review));
        response.setEmail(resolveReviewEmail(review));
        response.setPhone(review.getPhone());
        response.setOrderCode(review.getOrderCode());
        response.setStar(review.getStar());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setUserName(resolveReviewUserName(review));
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

    private String resolveProductName(ReviewEntity review) {
        if (hasText(review.getProductId())) {
            return productRepository.findById(review.getProductId())
                    .map(ProductEntity::getName)
                    .orElse(null);
        }
        return null;
    }

    private String resolveReviewEmail(ReviewEntity review) {
        if (hasText(review.getUserId())) {
            return userRepository.findById(review.getUserId())
                    .map(UserEntity::getEmail)
                    .orElse(null);
        }
        if (hasText(review.getOrderCode())) {
            return orderRepository.findByCode(review.getOrderCode())
                    .map(OrderEntity::getEmail)
                    .orElse(null);
        }
        return null;
    }
}
