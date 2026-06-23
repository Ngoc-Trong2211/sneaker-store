package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.coupon.CouponSpecificationRequest;
import com.example.sneaker_store.dto.request.coupon.CreateCouponRequest;
import com.example.sneaker_store.dto.request.coupon.UpdateCouponRequest;
import com.example.sneaker_store.dto.response.coupon.CreateCouponResponse;
import com.example.sneaker_store.dto.response.coupon.GetCouponResponse;
import com.example.sneaker_store.dto.response.coupon.UpdateCouponResponse;
import com.example.sneaker_store.dto.response.coupon.ValidateCouponResponse;
import com.example.sneaker_store.model.CouponEntity;
import com.example.sneaker_store.repository.CouponRepository;
import com.example.sneaker_store.service.CouponService;
import com.example.sneaker_store.util.enumEntity.CouponType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j(topic = "COUPON-SERVICE")
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('COUPON_CREATE') or or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public CreateCouponResponse createCoupon(CreateCouponRequest req) {
        String code = normalizeCode(req.getCode());
        validateCouponData(code, req.getType(), req.getDiscountValue(), req.getMinOrderValue(), req.getQuantity());
        if (this.couponRepository.existsByCodeIgnoreCase(code)) {
            throw new RuntimeException("Coupon code already exists: " + code);
        }
        CouponEntity coupon = new CouponEntity();
        coupon.setCode(code);
        coupon.setQuantity(req.getQuantity());
        coupon.setType(req.getType());
        coupon.setDiscountValue(req.getDiscountValue());
        coupon.setExpiresAt(req.getExpiresAt());
        coupon.setMinOrderValue(req.getMinOrderValue());
        this.couponRepository.save(coupon);
        return this.modelMapper.map(coupon, CreateCouponResponse.class);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('COUPON_UPDATE') or or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public UpdateCouponResponse updateCoupon(UpdateCouponRequest req) {
        CouponEntity coupon = this.couponRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + req.getId()));
        String code = normalizeCode(req.getCode());
        validateCouponData(code, req.getType(), req.getDiscountValue(), req.getMinOrderValue(), req.getQuantity());
        if (this.couponRepository.existsByCodeIgnoreCaseAndIdNot(code, req.getId())) {
            throw new RuntimeException("Coupon code already exists: " + code);
        }

        coupon.setCode(code);
        coupon.setQuantity(req.getQuantity());
        coupon.setType(req.getType());
        coupon.setDiscountValue(req.getDiscountValue());
        coupon.setExpiresAt(req.getExpiresAt());
        coupon.setMinOrderValue(req.getMinOrderValue());

        this.couponRepository.save(coupon);

        return this.modelMapper.map(coupon, UpdateCouponResponse.class);
    }

    @Override
    @PreAuthorize("hasAuthority('COUPON_READ') or or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public GetCouponResponse.Coupon getCouponById(Long id) {
        CouponEntity coupon = this.couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
        return this.modelMapper.map(coupon, GetCouponResponse.Coupon.class);
    }

    @Override
    @PreAuthorize("hasAuthority('COUPON_READ') or or hasAuthority('ADMIN') or hasAuthority('STAFF') or hasAuthority('USER')")
    public GetCouponResponse getCoupons(CouponSpecificationRequest request, Pageable pageable) {
        Page<CouponEntity> page = this.couponRepository.findAll(specCoupon(request), pageable);
        GetCouponResponse response = new GetCouponResponse();
        response.setDataPage(new GetCouponResponse.DataPage(
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages()
        ));
        response.setCoupons(page.getContent().stream()
                .map(coupon -> this.modelMapper.map(coupon, GetCouponResponse.Coupon.class))
                .toList());
        return response;
    }

    @Override
    @PreAuthorize("hasAuthority('COUPON_VALIDATE') or isAnonymous() or or hasAuthority('ADMIN') or hasAuthority('STAFF') or hasAuthority('USER')")
    public ValidateCouponResponse validateCoupon(String code, double orderAmount) {
        CouponEntity coupon = this.couponRepository.findByCodeIgnoreCase(normalizeCode(code))
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return buildValidationResponse(coupon, orderAmount);
    }

    @Override
    @Transactional
    public double useCoupon(String code, double orderAmount) {
        CouponEntity coupon = this.couponRepository.findByCodeForUpdate(normalizeCode(code))
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        ValidateCouponResponse validation = buildValidationResponse(coupon, orderAmount);
        if (!validation.isValid()) {
            throw new RuntimeException(validation.getMessage());
        }
        coupon.setQuantity(coupon.getQuantity() - 1);
        this.couponRepository.save(coupon);
        return validation.getDiscountAmount();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('COUPON_DELETE') or or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public void deleteCoupon(Long id) {
        CouponEntity coupon = this.couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
        this.couponRepository.delete(coupon);
    }

    private Specification<CouponEntity> specCoupon(CouponSpecificationRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }
            if (request.getCode() != null && !request.getCode().isBlank()) {
                predicates.add(cb.like(cb.upper(root.get("code")), "%" + request.getCode().trim().toUpperCase(Locale.ROOT) + "%"));
            }
            if (request.getType() != null) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }
            if (request.getActive() != null) {
                Instant now = Instant.now();
                if (request.getActive()) {
                    predicates.add(cb.greaterThan(root.get("quantity"), 0));
                    predicates.add(cb.greaterThan(root.get("expiresAt"), now));
                } else {
                    predicates.add(cb.or(
                            cb.lessThanOrEqualTo(root.get("quantity"), 0),
                            cb.lessThanOrEqualTo(root.get("expiresAt"), now)
                    ));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private ValidateCouponResponse buildValidationResponse(CouponEntity coupon, double orderAmount) {
        ValidateCouponResponse response = new ValidateCouponResponse();
        response.setCode(coupon.getCode());
        response.setType(coupon.getType());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setMinOrderValue(coupon.getMinOrderValue());
        response.setOrderAmount(orderAmount);
        response.setTotalAfterDiscount(orderAmount);

        if (orderAmount < coupon.getMinOrderValue()) {
            response.setValid(false);
            response.setMessage("Order amount must be at least " + coupon.getMinOrderValue());
            return response;
        }
        if (coupon.getQuantity() <= 0) {
            response.setValid(false);
            response.setMessage("Coupon is out of stock");
            return response;
        }
        if (coupon.getExpiresAt() != null && !coupon.getExpiresAt().isAfter(Instant.now())) {
            response.setValid(false);
            response.setMessage("Coupon has expired");
            return response;
        }

        double discountAmount = calculateDiscountAmount(coupon, orderAmount);
        response.setValid(true);
        response.setMessage("Coupon is valid");
        response.setDiscountAmount(discountAmount);
        response.setTotalAfterDiscount(Math.max(0, orderAmount - discountAmount));
        return response;
    }

    private double calculateDiscountAmount(CouponEntity coupon, double orderAmount) {
        if (CouponType.PERCENT.equals(coupon.getType())) {
            return Math.min(orderAmount, orderAmount * coupon.getDiscountValue() / 100.0);
        }
        return Math.min(orderAmount, coupon.getDiscountValue());
    }

    private void validateCouponData(String code, CouponType type, int discountValue, int minOrderValue, int quantity) {
        if (code.isBlank()) {
            throw new RuntimeException("Coupon code cannot be blank");
        }
        if (type == null) {
            throw new RuntimeException("Coupon type cannot be null");
        }
        if (discountValue <= 0) {
            throw new RuntimeException("Discount value must be greater than 0");
        }
        if (CouponType.PERCENT.equals(type) && discountValue > 100) {
            throw new RuntimeException("Percent coupon discount value cannot be greater than 100");
        }
        if (minOrderValue < 0) {
            throw new RuntimeException("Minimum order value cannot be negative");
        }
        if (quantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }
}
