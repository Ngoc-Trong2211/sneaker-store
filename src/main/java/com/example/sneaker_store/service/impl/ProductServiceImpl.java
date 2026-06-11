package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.*;
import com.example.sneaker_store.repository.*;
import com.example.sneaker_store.service.*;
import com.example.sneaker_store.specification.ProductSpecification;
import com.example.sneaker_store.util.enumEntity.ProductStatus;
import com.example.sneaker_store.util.enumEntity.VariantStatus;
import com.example.sneaker_store.util.exception.NameExistsException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.util.*;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.sneaker_store.dto.request.product.CreateProductRequest;
import com.example.sneaker_store.dto.request.product.SpecificationProductRequest;
import com.example.sneaker_store.dto.request.product.UpdateProductRequest;
import com.example.sneaker_store.dto.response.product.CreateProductResponse;
import com.example.sneaker_store.dto.response.product.GetProductByIdResponse;
import com.example.sneaker_store.dto.response.product.GetProductResponse;
import com.example.sneaker_store.dto.response.product.UpdateProductResponse;

@Service
@Slf4j(topic = "PRODUCT-SERVICE")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final ProductVariantRepository productVariantRepository;
    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;

    private String formatPriceToResponse(Double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(price);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRODUCT_CREATE') or hasAuthority('ADMIN')")
    public CreateProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            log.warn("Product with name '{}' already exists", request.getName());
            throw new NameExistsException("Product with the same name already exists");
        }
        BrandEntity brand = this.brandService.findById(request.getBrandId());
        CategoryEntity category = this.categoryService.findById(request.getCategoryId());
        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setStatus(ProductStatus.ACTIVE);
        product.setBrand(brand);
        product.setCategory(category);
        this.productRepository.save(product);

        CreateProductResponse res = this.modelMapper.map(product, CreateProductResponse.class);
        res.setPrice(formatPriceToResponse(request.getPrice()));
        res.setBrandName(product.getBrand().getName());
        res.setCategoryName(product.getCategory().getName());
        return res;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE') or hasAuthority('ADMIN')")
    public UpdateProductResponse updateProduct(UpdateProductRequest request) {
        ProductEntity product = this.productRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (this.productRepository.existsByNameAndIdNot(request.getName(), request.getId())) {
            throw new NameExistsException("Product with the same name already exists");
        }
        BrandEntity brand = this.brandService.findById(request.getBrandId());
        CategoryEntity category = this.categoryService.findById(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setTitle(request.getTitle());
        product.setPrice(Double.parseDouble(request.getPrice().replace(",", "")));
        product.setBrand(brand);
        product.setCategory(category);
        this.productRepository.save(product);
        UpdateProductResponse res = this.modelMapper.map(product, UpdateProductResponse.class);
        res.setPrice(formatPriceToResponse(Double.parseDouble(request.getPrice().replace(",", ""))));
        res.setBrandName(product.getBrand().getName());
        res.setCategoryName(product.getCategory().getName());

        return res;
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_READ') or isAnonymous() or hasAuthority('ADMIN')")
    public GetProductResponse getProducts(Pageable pageable, SpecificationProductRequest request, String guestId) {
        Specification<ProductEntity> specification = ProductSpecification.specProduct(request);
        Page<ProductEntity> productPage = this.productRepository.findAll(specification, pageable);

        GetProductResponse response = new GetProductResponse();
        response.setPage(this.modelMapper.map(response, GetProductResponse.DataPage.class));
        response.setProducts(productPage.map(product -> {
            GetProductResponse.Product prod = this.modelMapper.map(product, GetProductResponse.Product.class);
            List<GetProductResponse.Product.Variant> variants = product.getVariants() == null ?
                    new ArrayList<>() :
                    product.getVariants().stream().map(v -> {
                        GetProductResponse.Product.Variant rv = new GetProductResponse.Product.Variant();
                        rv.setId(v.getId());
                        rv.setColor(v.getColor());
                        List<GetProductResponse.Product.Variant.ProductSize> sizes = v.getSizes() == null
                                ? new ArrayList<>() :
                                v.getSizes().stream().map(size -> {
                                    GetProductResponse.Product.Variant.ProductSize rs=
                                            new GetProductResponse.Product.Variant.ProductSize();
                                    rs.setSize(size.getSize());
                                    rs.setQuantity(size.getQuantity());
                                    return rs;
                                }).toList();
                        rv.setSizes(sizes);
                        rv.setSku(v.getSku());
                        rv.setStock(v.getStock());
                        List<GetProductResponse.Product.Variant.ProductImage> images =
                                v.getImages() == null ? new ArrayList<>() :
                                        v.getImages().stream().map(img -> {
                                            GetProductResponse.Product.Variant.ProductImage ri =
                                                    new GetProductResponse.Product.Variant.ProductImage();
                                            ri.setMain(img.isMain());
                                            ri.setUrl(img.getImageURL());
                                            return ri;
                                        }).toList();
                        rv.setImages(images);
                        return rv;
                    }).toList();
            prod.setVariants(variants);
            if (product.getDiscount() != null) prod.setPercent(product.getDiscount().getPercent());
            prod.setPrice(formatPriceToResponse(product.getPrice()));
            prod.setBrandName(product.getBrand().getName());
            prod.setSlugCategory(product.getCategory().getSlug());
            String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
            Set<String> favouriteProductIds = new HashSet<>();
            if (email != null && !email.equals("anonymousUser")) {
                UserEntity user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

                favouriteProductIds = favouriteRepository.findProductIdsByUserId(user.getId());
            } else if (guestId != null) {
                favouriteProductIds = favouriteRepository.findProductIdsByGuestId(guestId);
            }
            prod.setFavourite(favouriteProductIds.contains(product.getId()));
            return prod;
        }).getContent());
        return response;
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_READ') or isAnonymous()")
    public GetProductByIdResponse getProductById(String slug, String guestId) {
        ProductEntity product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        GetProductByIdResponse res = modelMapper.map(product, GetProductByIdResponse.class);
        res.setPrice(formatPriceToResponse(product.getPrice()));
        if (product.getBrand() != null) {
            res.setBrandId(product.getBrand().getId());
            res.setBrandName(product.getBrand().getName());
        }
        if (product.getCategory() != null) {
            res.setCategoryId(product.getCategory().getId());
            res.setCategoryName(product.getCategory().getName());
            res.setCategorySlug(product.getCategory().getSlug());
        }
        List<GetProductByIdResponse.Variant> variants = product.getVariants() == null ?
            new ArrayList<>() :
            product.getVariants().stream().map(v -> {
                GetProductByIdResponse.Variant rv = new GetProductByIdResponse.Variant();
                rv.setId(v.getId());
                rv.setColor(v.getColor());
                List<GetProductByIdResponse.Variant.ProductSize> sizes = v.getSizes() == null
                        ? new ArrayList<>() :
                        v.getSizes().stream().map(size -> {
                            GetProductByIdResponse.Variant.ProductSize rs=
                                    new GetProductByIdResponse.Variant.ProductSize();
                            rs.setId(size.getId());
                            rs.setSize(size.getSize());
                            rs.setQuantity(size.getQuantity());
                            return rs;
                        }).toList();
                rv.setSizes(sizes);
                rv.setSku(v.getSku());
                rv.setStatus(String.valueOf(v.getStatus()));
                List<GetProductByIdResponse.Variant.ProductImage> images =
                        v.getImages() == null ? new ArrayList<>() :
                                v.getImages().stream().map(img -> {
                                    GetProductByIdResponse.Variant.ProductImage ri =
                                            new GetProductByIdResponse.Variant.ProductImage();
                                    ri.setMain(img.isMain());
                                    ri.setUrl(img.getImageURL());
                                    return ri;
                                }).toList();
                rv.setImages(images);
                return rv;
            }).toList();
        if (product.getDiscount() != null) res.setPercent(product.getDiscount().getPercent());
        res.setVariants(variants);
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        boolean favourite = false;
        if (email != null && !email.equals("anonymousUser")) {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            favourite = favouriteRepository.existsByUserIdAndProductId(user.getId(), product.getId());
        } else if (guestId != null) {
            favourite = favouriteRepository.existsByGuestIdAndProductId(guestId, product.getId());
        }
        res.setFavourite(favourite);
        return res;
    }

    @Override
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE_STATUS') or hasAuthority('ADMIN')")
    public void updateStatusProduct(String id, String status) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        if (ProductStatus.valueOf(status) == ProductStatus.SOLD_OUT && product.getQuantity() > 0) throw new RuntimeException("Quantity > 0");
        if (ProductStatus.valueOf(status) == ProductStatus.ACTIVE && product.getQuantity() == 0) throw new RuntimeException("Quantity = 0");
        product.setStatus(ProductStatus.valueOf(status));
        if (ProductStatus.valueOf(status)==(ProductStatus.ACTIVE)){
            Optional<List<ProductVariantEntity>> listVariants= this.productVariantRepository.findByProductId(id);
            if (listVariants.isPresent()){
                for(ProductVariantEntity prdVariant : listVariants.get()){
                    prdVariant.setStatus(VariantStatus.ACTIVE);
                }
            }
        }
        this.productRepository.save(product);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('PRODUCT_DELETE') or hasAuthority('ADMIN')")
    public void deleteProduct(String id) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        if (product.getStatus() == ProductStatus.DELETED) throw new RuntimeException("Product already deleted");
        this.productVariantRepository.deleteSoftProductVariant(product.getId());
        product.setStatus(ProductStatus.DELETED);
        this.productRepository.save(product);
    }
}
