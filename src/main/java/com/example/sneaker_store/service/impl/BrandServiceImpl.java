package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.model.request.brand.CreateBrandRequest;
import com.example.sneaker_store.model.request.brand.UpdateBrandRequest;
import com.example.sneaker_store.model.response.brand.CreateBrandResponse;
import com.example.sneaker_store.model.response.brand.GetBrandResponse;
import com.example.sneaker_store.model.response.brand.UpdateBrandResponse;
import com.example.sneaker_store.repository.BrandRepository;
import com.example.sneaker_store.service.BrandService;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.service.specification.BrandSpecification;
import com.example.sneaker_store.util.exception.NameExistsException;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j(topic = "BRAND-SERVICE")
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Value("${sneaker.upload-file.base-uri}")
    private String baseUri;

    public void deleteFile(String fileName) throws URISyntaxException {
        URI uri = new URI(baseUri + "brand" + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        file.delete();
    }

    @Override
    public BrandEntity findById(Long id) {
        BrandEntity brand = this.brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy brand!"));
        return brand;
    }

    @Override
    // @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    public CreateBrandResponse createBrand(CreateBrandRequest req) {
        BrandEntity brand = new BrandEntity();
        if (this.brandRepository.existsByName(req.getName().toUpperCase()))
            throw new NameExistsException("Name is exists");
        brand.setName(req.getName().toUpperCase());
        brand.setLogo(req.getLogo());
        this.brandRepository.save(brand);
        return this.modelMapper.map(brand, CreateBrandResponse.class);
    }

    @Override
    // @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    public UpdateBrandResponse updateBrand(UpdateBrandRequest req) throws URISyntaxException {
        BrandEntity brand = this.findById(req.getId());
        if (this.brandRepository.existsByNameAndIdNot(req.getName().toUpperCase(), req.getId())) {
            throw new RuntimeException("Tên brand đã tồn tại!");
        }
        if (!brand.getName().equals(req.getName().toUpperCase())){
            brand.setName(req.getName().toUpperCase());
        }
        long fileLength = this.fileService.existFile(brand.getLogo(), "brand");
        if (fileLength == 0) {
            throw new IdInvalidException("Khong ton tai ten file");
        }
        else this.deleteFile(brand.getLogo());
        brand.setLogo(req.getLogo());
        this.brandRepository.save(brand);

        return this.modelMapper.map(brand, UpdateBrandResponse.class);
    }

    @Override
    // @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    public GetBrandResponse getBrand(Pageable pageable, String name) {
        Specification<BrandEntity> spec = BrandSpecification.specBrand(name);
        Page<BrandEntity> page = this.brandRepository.findAll(spec, pageable);

        GetBrandResponse res = new GetBrandResponse();

        GetBrandResponse.DataPage pageRes =
                this.modelMapper.map(page, GetBrandResponse.DataPage.class);
        res.setDataPage(pageRes);

        List<GetBrandResponse.Brand> brands = page.getContent().stream()
                .map(item -> this.modelMapper.map(item, GetBrandResponse.Brand.class))
                .toList();
        res.setBrands(brands);

        return res;
    }

    @Override
    // @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    public void deleteBrand(Long id) throws URISyntaxException {
        BrandEntity brand = this.findById(id);
        this.deleteFile(brand.getLogo());
        this.brandRepository.delete(brand);
    }
}
