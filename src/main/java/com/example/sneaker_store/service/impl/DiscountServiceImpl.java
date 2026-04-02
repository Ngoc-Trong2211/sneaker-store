package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.DiscountEntity;
import com.example.sneaker_store.model.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.model.response.discount.CreateDiscountResponse;
import com.example.sneaker_store.repository.DiscountRepository;
import com.example.sneaker_store.service.DiscountService;
import com.example.sneaker_store.util.enumEntity.DiscountStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "DISCOUNT-SERVICE")
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
        private final DiscountRepository discountRepository;
        private final ModelMapper modelMapper;

        @Override
        public CreateDiscountResponse createDiscount(CreateDiscountRequest request) {
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
}
