package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.model.CategoryEntity;
import com.example.sneaker_store.model.DiscountEntity;
import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.dto.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.dto.request.discount.DiscountSpecificationRequest;
import com.example.sneaker_store.dto.request.discount.UpdateDiscountRequest;
import com.example.sneaker_store.dto.response.discount.CreateDiscountResponse;
import com.example.sneaker_store.dto.response.discount.GetDiscountResponse;
import com.example.sneaker_store.dto.response.discount.GetDiscountResponse.Discount;
import com.example.sneaker_store.dto.response.discount.UpdateDiscountResponse;
import com.example.sneaker_store.repository.DiscountRepository;
import com.example.sneaker_store.repository.ProductRepository;
import com.example.sneaker_store.service.BrandService;
import com.example.sneaker_store.service.CategoryService;
import com.example.sneaker_store.service.DiscountService;
import com.example.sneaker_store.specification.DiscountSpecification;
import com.example.sneaker_store.util.enumEntity.DiscountStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "DISCOUNT-SERVICE")
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductRepository productRepository;

    private List<ProductEntity> getProductByApply(String applyFor, String nameApply) {
        return switch (applyFor.toUpperCase()) {
            case "CATEGORY" -> {
                CategoryEntity category = categoryService.findByName(nameApply);
                if (category == null) throw new RuntimeException("Category not found with name: " + nameApply);
                yield productRepository.findByCategoryId(category.getId());
            }
            case "BRAND" -> {
                BrandEntity brand = brandService.findByName(nameApply);
                if (brand == null) throw new RuntimeException("Brand not found with name: " + nameApply);
                yield productRepository.findByBrandId(brand.getId());
            }
            case "ALL" -> productRepository.findAll();
            default -> throw new RuntimeException("Invalid applyFor value: " + applyFor);
        };
    }

    @Override
    @Transactional
    public CreateDiscountResponse createDiscount(CreateDiscountRequest request) {
        if (this.discountRepository.existsOverlap(
                request.getApplyFor(), request.getNameApply(), request.getEndTime(), request.getStartTime()))
            throw new RuntimeException("Discount with nameApply already exists: " + request.getNameApply());
        DiscountEntity discount = new DiscountEntity();
        discount.setPercent(request.getPercent()); 
        discount.setDescription(request.getDescription());
        discount.setStartTime(request.getStartTime());
        discount.setEndTime(request.getEndTime());
        discount.setStatus(DiscountStatus.ACTIVE);
        discount.setApplyFor(request.getApplyFor());
        discount.setNameApply(request.getNameApply());
        this.discountRepository.save(discount);

        List<ProductEntity> products = getProductByApply(request.getApplyFor(), request.getNameApply());
        products.forEach(p -> p.setDiscount(discount));
        this.productRepository.saveAll(products);
        return this.modelMapper.map(discount, CreateDiscountResponse.class);
    }

    @Override
    @Transactional
    public UpdateDiscountResponse updateDiscount(UpdateDiscountRequest request) {
        DiscountEntity discount = this.discountRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + request.getId()));
        if (this.discountRepository.existsOverlap(
                request.getApplyFor(), request.getNameApply(), request.getEndTime(), request.getStartTime())
            && !discount.getId().equals(request.getId())){
            throw new RuntimeException(
                    "Discount with nameApply already exists: " + request.getNameApply());
        }
        discount.setPercent(request.getPercent());
        discount.setDescription(request.getDescription());
        discount.setStartTime(request.getStartTime());
        discount.setEndTime(request.getEndTime());
        discount.setApplyFor(request.getApplyFor());
        discount.setNameApply(request.getNameApply());
        this.discountRepository.save(discount);

        List<ProductEntity> oldProducts = productRepository.findByDiscountId(discount.getId());
        oldProducts.forEach(p -> p.setDiscount(null));
        List<ProductEntity> products = getProductByApply(request.getApplyFor(), request.getNameApply());
        products.forEach(p -> p.setDiscount(discount));
        this.productRepository.saveAll(oldProducts);
        this.productRepository.saveAll(products);
        return this.modelMapper.map(discount, UpdateDiscountResponse.class);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void updateExpiredDiscounts() {
        log.info("Running scheduled task to update expired discounts");
        this.discountRepository.updateExpiredDiscounts(Instant.now());
        this.productRepository.autoClearDiscountFromProducts();
    }

    @Override
    public Discount getDiscountById(String id) {
        DiscountEntity discount = this.discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));
        return this.modelMapper.map(discount, GetDiscountResponse.Discount.class);
    }

    @Override
    public void updateStatusDiscount(String id, DiscountStatus status) {
        DiscountEntity discount = this.discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));
        if (this.discountRepository.checkEndTimeBeforeCurrentTime(id, Instant.now())) {
            throw new RuntimeException("Cannot update status of an expired discount");
        }
        discount.setStatus(status);
        this.discountRepository.save(discount);
    }

    @Override
    public GetDiscountResponse getDiscounts(DiscountSpecificationRequest request, Pageable pageable) {
        Specification<DiscountEntity> spec = DiscountSpecification.specDiscount(request);
        Page<DiscountEntity> discountPage = this.discountRepository.findAll(spec, pageable);
        GetDiscountResponse response = new GetDiscountResponse();
        response.setPage(this.modelMapper.map(discountPage, GetDiscountResponse.DataPage.class));
        response.setDiscounts(discountPage.map(discount -> this.modelMapper.map(discount, GetDiscountResponse.Discount.class)).getContent());
        return response;
    }

    @Override
    @Transactional
    public void deleteDiscount(String id) {
        DiscountEntity discount = this.discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));
        this.productRepository.clearDiscountFromProducts(discount.getId());
        this.discountRepository.delete(discount);
    }
}
