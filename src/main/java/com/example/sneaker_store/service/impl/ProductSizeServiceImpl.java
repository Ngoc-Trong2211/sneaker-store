package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.UpdateSizeRequest;
import com.example.sneaker_store.model.ProductSizeEntity;
import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.repository.ProductRepository;
import com.example.sneaker_store.repository.ProductSizeRepository;
import com.example.sneaker_store.repository.ProductVariantRepository;
import com.example.sneaker_store.service.ProductSizeService;
import com.example.sneaker_store.util.enumEntity.SizeStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "PRODUCT-SIZE-SERVICE")
@RequiredArgsConstructor
public class ProductSizeServiceImpl implements ProductSizeService {
    private final ProductSizeRepository productSizeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public void updateSize(String variantId, Long sizeId, String sizeReq, Integer quantity) {
        ProductVariantEntity variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        ProductSizeEntity size = productSizeRepository.findById(sizeId)
                .orElseThrow(() -> new RuntimeException("Size not found"));
        ProductSizeEntity existsSize = productSizeRepository
                .findByVariantIdAndSizeAndIdNot(
                        variant.getId(),
                        sizeReq,
                        size.getId()
                );
        if (existsSize != null) {
            existsSize.setQuantity(existsSize.getQuantity() + quantity);
            size.setQuantity(0);
            size.setStatus(SizeStatus.SOLD_OUT);

            productSizeRepository.save(existsSize);
            productSizeRepository.save(size);
        }
        else {
            size.setSize(sizeReq);
            size.setQuantity(quantity);
            if (quantity <= 0) {
                size.setStatus(SizeStatus.SOLD_OUT);
            }
            else size.setStatus(SizeStatus.ACTIVE);
            productSizeRepository.save(size);
        }
        int totalStock = variant.getSizes().stream()
                .filter(s -> s.getStatus() == SizeStatus.ACTIVE)
                .mapToInt(ProductSizeEntity::getQuantity)
                .sum();

        variant.setStock(totalStock);

        int totalProductQuantity = variant.getProduct()
                .getVariants()
                .stream()
                .mapToInt(ProductVariantEntity::getStock)
                .sum();
        variant.getProduct().setQuantity(totalProductQuantity);
        productVariantRepository.save(variant);
        productRepository.save(variant.getProduct());
    }
}
