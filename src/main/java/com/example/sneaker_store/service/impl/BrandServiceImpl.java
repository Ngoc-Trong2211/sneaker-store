package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.dto.request.brand.CreateBrandRequest;
import com.example.sneaker_store.dto.request.brand.SpecificationBrandRequest;
import com.example.sneaker_store.dto.request.brand.UpdateBrandRequest;
import com.example.sneaker_store.dto.response.brand.CreateBrandResponse;
import com.example.sneaker_store.dto.response.brand.GetBrandResponse;
import com.example.sneaker_store.dto.response.brand.UpdateBrandResponse;
import com.example.sneaker_store.repository.BrandRepository;
import com.example.sneaker_store.service.BrandService;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.specification.BrandSpecification;
import com.example.sneaker_store.util.exception.NameExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j(topic = "BRAND-SERVICE")
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Override
    public BrandEntity findById(Long id) {
        return this.brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy brand!"));
    }
    
    @Override
    public BrandEntity findByName(String name) {
        return this.brandRepository.findByName(name)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy brand!"));
    }

    @Override
    @PreAuthorize("hasAuthority('BRAND_CREATE') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public CreateBrandResponse createBrand(CreateBrandRequest req) {
        BrandEntity brand = new BrandEntity();
        if (this.brandRepository.existsByName(req.getName().toUpperCase()))
            throw new NameExistsException("Name is exists");
        brand.setName(req.getName().toUpperCase());
        brand.setLogo(req.getLogo());
        brand.setCountryCode(req.getCountryCode());
        brand.setPublicId(req.getPublicId());

        Locale obj = new Locale("vi", req.getCountryCode());
        Locale viLocale = new Locale("vi", "VN");
        brand.setCountry(obj.getDisplayCountry(viLocale));

        this.brandRepository.save(brand);
        return this.modelMapper.map(brand, CreateBrandResponse.class);
    }


//    String[] countryCodes = Locale.getISOCountries();
//
//            for (String countryCode : countryCodes) {
//
//        Locale obj = new Locale("vi", countryCode);
//        Locale viLocale = new Locale("vi", "VN");
//
//        System.out.println("Country Code = " + obj.getCountry()
//                + ", Country Name = " + obj.getDisplayCountry(viLocale));
//
//    }

    @Override
    @PreAuthorize("hasAuthority('BRAND_UPDATE') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public UpdateBrandResponse updateBrand(UpdateBrandRequest req){
        BrandEntity brand = this.findById(req.getId());

        if (this.brandRepository.existsByNameAndIdNot(req.getName().toUpperCase(), req.getId())) {
            throw new RuntimeException("Tên brand đã tồn tại!");
        }

        brand.setName(req.getName().toUpperCase());

        if (req.getLogo() != null && !req.getLogo().equals(brand.getLogo())) {
            this.fileService.deleteFile(brand.getPublicId());
            brand.setLogo(req.getLogo());
            brand.setPublicId(req.getPublicId());
        }

        brand.setCountryCode(req.getCountryCode());

        Locale obj = new Locale("vi", req.getCountryCode());
        Locale viLocale = new Locale("vi", "VN");
        brand.setCountry(obj.getDisplayCountry(viLocale));

        this.brandRepository.save(brand);

        return this.modelMapper.map(brand, UpdateBrandResponse.class);
    }

    @Override
    @PreAuthorize("hasAuthority('BRAND_READ') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public GetBrandResponse getBrand(Pageable pageable, SpecificationBrandRequest request) {
        Specification<BrandEntity> spec = BrandSpecification.specBrand(request);
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
    public List<GetBrandResponse.Brand> getAll() {
        List<BrandEntity> brands = this.brandRepository.findAll();

        return brands.stream().map(item -> this.modelMapper.map(item, GetBrandResponse.Brand.class))
                .toList();
    }

    @Override
    @PreAuthorize("hasAuthority('BRAND_DELETE') or hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public void deleteBrand(Long id) {
        BrandEntity brand = this.findById(id);
        this.fileService.deleteFile(brand.getPublicId());
        this.brandRepository.delete(brand);
    }
}
