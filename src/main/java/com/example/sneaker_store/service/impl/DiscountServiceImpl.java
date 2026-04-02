package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.model.CategoryEntity;
import com.example.sneaker_store.model.DiscountEntity;
import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.model.request.discount.DiscountSpecificationRequest;
import com.example.sneaker_store.model.request.discount.UpdateDiscountRequest;
import com.example.sneaker_store.model.response.discount.CreateDiscountResponse;
import com.example.sneaker_store.model.response.discount.GetDiscountResponse;
import com.example.sneaker_store.model.response.discount.GetDiscountResponse.Discount;
import com.example.sneaker_store.model.response.discount.UpdateDiscountResponse;
import com.example.sneaker_store.repository.DiscountRepository;
import com.example.sneaker_store.repository.ProductRepository;
import com.example.sneaker_store.service.BrandService;
import com.example.sneaker_store.service.CategoryService;
import com.example.sneaker_store.service.DiscountService;
import com.example.sneaker_store.service.specification.DiscountSpecification;
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

@Service
@Slf4j(topic = "DISCOUNT-SERVICE")
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductRepository productRepository;

    @Override
    public CreateDiscountResponse createDiscount(CreateDiscountRequest request) {
        if (this.discountRepository.existsByNameApply(request.getNameApply())) {
            throw new RuntimeException("Discount with nameApply already exists: " + request.getNameApply());
        }
        DiscountEntity discount = new DiscountEntity();
        discount.setPercent(request.getPercent()); 
        discount.setDescription(request.getDescription());
        discount.setStartTime(request.getStartTime());
        discount.setEndTime(request.getEndTime());
        discount.setStatus(DiscountStatus.ACTIVE);
        discount.setApplyFor(request.getApplyFor());
        discount.setNameApply(request.getNameApply());
        this.discountRepository.save(discount);

        List<ProductEntity> products;
        switch (request.getApplyFor().toUpperCase()) {
            case "CATEGORY":
                CategoryEntity category = categoryService.findByName(request.getNameApply());
                products = productRepository.findByCategoryId(category.getId());
                break;
            case "BRAND":
                BrandEntity brand = brandService.findByName(request.getNameApply());
                products = productRepository.findByBrandId(brand.getId());
                break;
            case "ALL":
                products = productRepository.findAll();
                break;
            default:
                throw new RuntimeException("Invalid applyFor value: " + request.getApplyFor());
        }
        products.forEach(p -> p.setDiscount(discount));
        this.productRepository.saveAll(products);
        return this.modelMapper.map(discount, CreateDiscountResponse.class);
    }

    @Override
    public UpdateDiscountResponse updateDiscount(UpdateDiscountRequest request) {
        DiscountEntity discount = this.discountRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + request.getId()));
        if (this.discountRepository.existsByNameApply(request.getNameApply()) 
            && !discount.getNameApply().equals(request.getNameApply())) {
            throw new RuntimeException("Discount with nameApply already exists: " + request.getNameApply());
        }
        discount.setPercent(request.getPercent());
        discount.setDescription(request.getDescription());
        discount.setStartTime(request.getStartTime());
        discount.setEndTime(request.getEndTime());
        discount.setApplyFor(request.getApplyFor());
        discount.setNameApply(request.getNameApply());
        this.discountRepository.save(discount);

        List<ProductEntity> products;
        switch (request.getApplyFor().toUpperCase()) {
            case "CATEGORY":
                CategoryEntity category = categoryService.findByName(request.getNameApply());
                products = productRepository.findByCategoryId(category.getId());
                break;
            case "BRAND":
                BrandEntity brand = brandService.findByName(request.getNameApply());
                products = productRepository.findByBrandId(brand.getId());
                break;
            case "ALL":
                products = productRepository.findAll();
                break;
            default:
                throw new RuntimeException("Invalid applyFor value: " + request.getApplyFor());
        }
        products.forEach(p -> p.setDiscount(discount));
        this.productRepository.saveAll(products);
        return this.modelMapper.map(discount, UpdateDiscountResponse.class);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void updateExpiredDiscounts() {
        log.info("Running scheduled task to update expired discounts");
        this.discountRepository.updateExpiredDiscounts(Instant.now());
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
    public void deleteDiscount(String id) {
        DiscountEntity discount = this.discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));
        List<ProductEntity> products = this.productRepository.findByDiscountId(discount.getId());
        products.forEach(p -> p.setDiscount(null));
        this.productRepository.saveAll(products);
        this.discountRepository.delete(discount);
    }
}
