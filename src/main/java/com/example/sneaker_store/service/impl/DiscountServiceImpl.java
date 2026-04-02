package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.DiscountEntity;
import com.example.sneaker_store.model.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.model.request.discount.UpdateDiscountRequest;
import com.example.sneaker_store.model.response.discount.CreateDiscountResponse;
import com.example.sneaker_store.model.response.discount.GetDiscountResponse;
import com.example.sneaker_store.model.response.discount.GetDiscountResponse.Discount;
import com.example.sneaker_store.model.response.discount.UpdateDiscountResponse;
import com.example.sneaker_store.repository.DiscountRepository;
import com.example.sneaker_store.service.DiscountService;
import com.example.sneaker_store.util.enumEntity.DiscountStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "DISCOUNT-SERVICE")
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
        private final DiscountRepository discountRepository;
        private final ModelMapper modelMapper;

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
            return this.modelMapper.map(discount, UpdateDiscountResponse.class);
        }

        @Scheduled(cron = "0 0 * * * ?")
        public void updateExpiredDiscounts() {
            log.info("Running scheduled task to update expired discounts");
            this.discountRepository.updateExpiredDiscounts();
        }

        @Override
        public Discount getDiscountById(String id) {
            DiscountEntity discount = this.discountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));
            return this.modelMapper.map(discount, GetDiscountResponse.Discount.class);
        }

        
}
