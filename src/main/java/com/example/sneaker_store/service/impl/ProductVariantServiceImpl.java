package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.response.productVariant.*;
import com.example.sneaker_store.model.ProductImageEntity;
import com.example.sneaker_store.model.ProductSizeEntity;
import com.example.sneaker_store.repository.ProductImageRepository;
import com.example.sneaker_store.repository.ProductSizeRepository;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.service.ProductImageService;
import com.example.sneaker_store.service.ProductSizeService;
import com.example.sneaker_store.service.ProductVariantService;
import com.example.sneaker_store.specification.ProductVariantSpecification;
import com.example.sneaker_store.util.SkuGenerator;
import com.example.sneaker_store.util.enumEntity.ProductStatus;
import com.example.sneaker_store.util.enumEntity.SizeStatus;
import com.example.sneaker_store.util.enumEntity.VariantStatus;

import jakarta.transaction.Transactional;

import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.dto.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.dto.request.productVariant.SpecificationProductVariantRequest;
import com.example.sneaker_store.dto.request.productVariant.UpdateProductVariantRequest;
import com.example.sneaker_store.repository.ProductRepository;
import com.example.sneaker_store.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "PRODUCT-VARIANT-SERVICE")
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileService fileService;
    private final ProductImageService productImageService;
    private final ProductSizeRepository productSizeRepository;
    private final ProductSizeService productSizeService;

    private String formatPriceToResponse(Double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(price);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRODUCT_CREATE') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public CreateProductVariantResponse createProductVariant(CreateProductVariantRequest request) {
        ProductEntity product = this.productRepository.findByName(request.getProductName()).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", request.getProductName());
            return new RuntimeException("Sản phẩm không tồn tại");
        });
        if (product.getStatus() != ProductStatus.ACTIVE) throw new RuntimeException("Sản phẩm không hoạt động");
        ProductVariantEntity existedVariant =
                productVariantRepository.findByColorAndProductId(
                        request.getColor(),
                        product.getId()
                );
        if (existedVariant != null) {
            throw new RuntimeException("Màu sắc đã tồn tại");
        }

        ProductVariantEntity variant = new ProductVariantEntity();
        variant.setColor(request.getColor());
        variant.setProduct(product);
        variant.setSku(
                SkuGenerator.generate(
                        product.getBrand().getName(),
                        product.getName(),
                        request.getColor(),
                        "SIZE"
                ) + "-" + System.currentTimeMillis()
        );
        variant = productVariantRepository.save(variant);

        int totalStock = 0;
        for (CreateProductVariantRequest.SizeRequest req : request.getSizes()){
            if (req.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng phải lớn hơn 0");
            }
            ProductSizeEntity sizeEntity = new ProductSizeEntity();

            sizeEntity.setSize(req.getSize());
            sizeEntity.setQuantity(req.getQuantity());
            sizeEntity.setVariant(variant);
            sizeEntity.setStatus(SizeStatus.ACTIVE);
            totalStock += req.getQuantity();
            this.productSizeRepository.save(sizeEntity);
        }
        variant.setStock(totalStock);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > 6) {
                log.warn("Too many images provided for product");
                throw new IllegalArgumentException("Chỉ được phép tải lên tối đa 6 ảnh");
            }

            else{
                for (int i=0; i<request.getImages().size(); i++){
                    ProductImageEntity image = this.productImageRepository.findByImageURL(request.getImages().get(i));
                    if (image == null) {
                        throw new RuntimeException("Không tìm thấy ảnh có URL: " + request.getImages().get(i));
                    }
                    image.setMain(i == 0);
                    image.setVariant(variant);
                    System.out.println("save");
                    this.productImageRepository.save(image);
                }
            }
        }
        this.productVariantRepository.save(variant);
        product.setQuantity(product.getQuantity() + totalStock);
        this.productRepository.save(product);
        return modelMapper.map(variant, CreateProductVariantResponse.class);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public UpdateProductVariantResponse updateProductVariant(UpdateProductVariantRequest request) {
        ProductVariantEntity variant = productVariantRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Biến thể sản phẩm không tồn tại"));
        ProductEntity product = productRepository.findByName(request.getProductName())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        if (product.getStatus() == ProductStatus.DELETED)
            throw new RuntimeException("Sản phẩm không hoạt động");
        ProductVariantEntity duplicate = productVariantRepository
                .findByColorAndProductId(request.getColor(), product.getId());
        if (duplicate != null && !duplicate.getId().equals(variant.getId())) {
            throw new RuntimeException("Biến thể sản phẩm đã tồn tại");
        }
        variant.setColor(request.getColor());
        variant.setProduct(product);
        variant.setSku(SkuGenerator.generate(
                product.getBrand().getName(),
                product.getName(),
                request.getColor(),
                "SIZE"
        ) + "-" + System.currentTimeMillis());
        variant = productVariantRepository.save(variant);

        List<UpdateProductVariantRequest.SizeRequest> newSizes = request.getSizes();
        if (newSizes.isEmpty()){
            int removedQty = variant.getSizes().stream()
                    .mapToInt(ProductSizeEntity::getQuantity)
                    .sum();
            product.setQuantity(product.getQuantity() - removedQty);
            variant.setStock(0);
            variant.getSizes().clear();
        }
        else{
            List<ProductSizeEntity> currentSizes = variant.getSizes();
            Map<Long, ProductSizeEntity> currentSizeMap = currentSizes.stream()
                    .collect(Collectors.toMap(ProductSizeEntity::getId, s -> s));
            List<Long> newSizeIds = newSizes.stream()
                    .map(UpdateProductVariantRequest.SizeRequest::getId)
                    .filter(Objects::nonNull)
                    .toList();
            List<ProductSizeEntity> removeSizes = currentSizes.stream()
                    .filter(size -> !newSizeIds.contains(size.getId()))
                    .toList();

            for (ProductSizeEntity s : removeSizes) {
                product.setQuantity(product.getQuantity() - s.getQuantity());
                variant.setStock(variant.getStock() - s.getQuantity());
                variant.getSizes().remove(s);
            }

            for (UpdateProductVariantRequest.SizeRequest reqSize : newSizes) {
                if (reqSize.getId() != null && currentSizeMap.containsKey(reqSize.getId())) {
                    ProductSizeEntity currentSize = currentSizeMap.get(reqSize.getId());
                    this.productSizeService.updateSize(
                            variant.getId(), currentSize.getId(), reqSize.getSize(), reqSize.getQuantity());
                }
                else {
                    boolean exists = currentSizes.stream()
                            .anyMatch(s -> s.getSize().equals(reqSize.getSize()));
                    if (exists) throw new RuntimeException("Kích cỡ " + reqSize.getSize() + " đã tồn tại");
                    ProductSizeEntity newSize = new ProductSizeEntity();
                    newSize.setSize(reqSize.getSize());
                    newSize.setQuantity(reqSize.getQuantity());
                    newSize.setVariant(variant);
                    newSize.setStatus(SizeStatus.ACTIVE);
                    product.setQuantity(product.getQuantity() + reqSize.getQuantity());
                    variant.setStock(variant.getStock() + reqSize.getQuantity());
                    variant.getSizes().add(newSize);
                }
            }
        }

        List<String> newImageUrls = request.getImages();
        List<ProductImageEntity> currentImages = variant.getImages();
        List<ProductImageEntity> toRemove = currentImages.stream()
                .filter(img -> !newImageUrls.contains(img.getImageURL()))
                .toList();
        for (ProductImageEntity img : toRemove) {
            this.fileService.deleteFile(img.getPublicId());
            currentImages.remove(img);
            this.productImageRepository.delete(img);
        }
        for (int i = 0; i < newImageUrls.size(); i++) {
            String url = newImageUrls.get(i);
            boolean exists = currentImages.stream()
                    .anyMatch(img -> img.getImageURL().equals(url));
            if (!exists) {
                ProductImageEntity image = this.productImageRepository.findByImageURL(url);
                if (image == null) {
                    throw new RuntimeException("Không tìm thấy ảnh có URL: " + url);
                }
                image.setVariant(variant);
                image.setMain(i == 0);
                this.productImageRepository.save(image);
                currentImages.add(image);
            } else {
                int finalI = i;
                currentImages.stream()
                        .filter(img -> img.getImageURL().equals(url))
                        .forEach(img -> img.setMain(finalI == 0));
            }
        }

        int totalVariantStock = variant.getSizes().stream()
                .filter(s -> s.getStatus() == SizeStatus.ACTIVE)
                .mapToInt(ProductSizeEntity::getQuantity)
                .sum();
        variant.setStock(totalVariantStock);

        int totalProductStock = product.getVariants().stream()
                .mapToInt(ProductVariantEntity::getStock)
                .sum();
        product.setQuantity(totalProductStock);

        if (variant.getStock() > 0) {
            variant.setStatus(VariantStatus.ACTIVE);
        } else {
            variant.setStatus(VariantStatus.SOLD_OUT);
        }

        if (product.getQuantity() > 0) {
            product.setStatus(ProductStatus.ACTIVE);
        } else {
            product.setStatus(ProductStatus.SOLD_OUT);
        }

        this.productVariantRepository.save(variant);
        this.productRepository.save(product);

        return modelMapper.map(variant, UpdateProductVariantResponse.class);
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_VARIANT_READ') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public GetVariantByIdResponse getVariantById(String id) {
        ProductVariantEntity variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Biến thể sản phẩm không tồn tại"));
        GetVariantByIdResponse res = this.modelMapper.map(variant, GetVariantByIdResponse.class);
        res.setProductName(variant.getProduct().getName());
        res.setBrandName(variant.getProduct().getBrand().getName());
        List<GetVariantByIdResponse.ProductSize> sizes = variant.getSizes() == null
                ? new ArrayList<>() :
                variant.getSizes().stream().map(size -> {
                    GetVariantByIdResponse.ProductSize rs=
                            new GetVariantByIdResponse.ProductSize();
                    rs.setId(size.getId());
                    rs.setSize(size.getSize());
                    rs.setQuantity(size.getQuantity());
                    return rs;
                }).toList();
        res.setSizes(sizes);
        List<GetVariantByIdResponse.ProductImage> listResImg = new ArrayList<>();
        for (ProductImageEntity img : variant.getImages()){
            GetVariantByIdResponse.ProductImage imgRes = new GetVariantByIdResponse.ProductImage();
            imgRes.setUrl(img.getImageURL());
            imgRes.setMain(img.isMain());
            listResImg.add(imgRes);
        }
        res.setImages(listResImg);
        return res;
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_VARIANT_READ') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public GetVariantBySkuResponse getVariantBySku(String sku) {
        ProductVariantEntity variant = this.productVariantRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Biến thể sản phẩm không tồn tại"));
        GetVariantBySkuResponse res = this.modelMapper.map(variant, GetVariantBySkuResponse.class);
        GetVariantBySkuResponse.Product resPrd = new GetVariantBySkuResponse.Product();
        List<GetVariantBySkuResponse.ProductImage> listResImg = new ArrayList<>();
        for (ProductImageEntity img : variant.getImages()){
            GetVariantBySkuResponse.ProductImage imgRes = new GetVariantBySkuResponse.ProductImage();
            imgRes.setUrl(img.getImageURL());
            imgRes.setMain(img.isMain());
            listResImg.add(imgRes);
        }
        res.setImages(listResImg);
        ProductEntity product = variant.getProduct();

        resPrd.setId(product.getId());
        resPrd.setName(product.getName());
        resPrd.setDescription(product.getDescription());
        resPrd.setPrice(formatPriceToResponse(product.getPrice()));
        resPrd.setStatus(product.getStatus().name());
        resPrd.setQuantity(String.valueOf(product.getQuantity()));
        resPrd.setSlug(product.getSlug());
        resPrd.setSlugCategory(product.getCategory().getSlug());
        resPrd.setBrandName(product.getBrand().getName());
        resPrd.setTitle(product.getTitle());
        res.setProduct(resPrd);

        return res;
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_VARIANT_READ') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public GetProductVariantResponse getProductVariant(Pageable pageable, SpecificationProductVariantRequest request) {
        Specification<ProductVariantEntity> specification = ProductVariantSpecification.specVariant(request);
        Page<ProductVariantEntity> productVariantPage = this.productVariantRepository.findAll(specification, pageable);
        GetProductVariantResponse response = new GetProductVariantResponse();
        response.setPage(this.modelMapper.map(productVariantPage, GetProductVariantResponse.DataPage.class));
        response.setProductVariants(productVariantPage.getContent().stream().map(
            productVariant -> {
                GetProductVariantResponse.ProductVariant resVariant = this.modelMapper.map(
                        productVariant, GetProductVariantResponse.ProductVariant.class);
                for (ProductImageEntity img : productVariant.getImages()){
                    if (img.isMain()){
                        resVariant.setImage(img.getImageURL());
                        break;
                    }
                }
                resVariant.setProductName(productVariant.getProduct().getName());
                resVariant.setBrandName(productVariant.getProduct().getBrand().getName());
                return resVariant;
            }).toList());
        return response;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRODUCT_DELETE') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public void deleteProductVariant(String id) {
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(id).orElse(null);
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", id);
            throw new RuntimeException("Biến thể sản phẩm không tồn tại");
        }
        ProductEntity product = this.productRepository.findById(existingVariant.getProduct().getId()).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", existingVariant.getProduct().getId());
            return new RuntimeException("Sản phẩm không tồn tại");
        });
        product.setQuantity(product.getQuantity() - existingVariant.getStock());
        productRepository.save(product);
        List<ProductImageEntity> images = this.productImageRepository.findByVariantId(id).orElse(List.of());
        for (ProductImageEntity image : images) {
            if (!image.isMain()) {
                this.fileService.deleteFile(image.getPublicId());
                this.productImageService.deleteProductImage(image.getId());
            }
        }
        List<ProductSizeEntity> sizes = this.productSizeRepository.findByVariantId(id).orElse(List.of());
        for (ProductSizeEntity size : sizes) {
            size.setStatus(SizeStatus.DELETED);
            this.productSizeRepository.save(size);
        }
        existingVariant.setStatus(VariantStatus.DELETED);
        this.productVariantRepository.save(existingVariant);
    }  

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE_STATUS') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public void updateProductVariantStatus(String id, String status) {
        if (VariantStatus.valueOf(status) == VariantStatus.DELETED) {
            this.deleteProductVariant(id);
            return;
        }
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(id).orElse(null);
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", id);
            throw new RuntimeException("Biến thể sản phẩm không tồn tại");
        }
        if (VariantStatus.valueOf(status) == VariantStatus.SOLD_OUT && existingVariant.getStock() > 0) throw new RuntimeException("Không thể đặt trạng thái hết hàng khi tồn kho vẫn lớn hơn 0");
        existingVariant.setStatus(VariantStatus.valueOf(status));
        this.productVariantRepository.save(existingVariant);
    }
}
