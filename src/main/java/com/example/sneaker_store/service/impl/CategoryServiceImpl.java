package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.CategoryEntity;
import com.example.sneaker_store.dto.request.category.CreateCategoryRequest;
import com.example.sneaker_store.dto.request.category.UpdateCategoryRequest;
import com.example.sneaker_store.dto.response.category.CreateCategoryResponse;
import com.example.sneaker_store.dto.response.category.GetCategoryResponse;
import com.example.sneaker_store.dto.response.category.UpdateCategoryResponse;
import com.example.sneaker_store.repository.CategoryRepository;
import com.example.sneaker_store.service.CategoryService;
import com.example.sneaker_store.specification.CategorySpecification;
import com.example.sneaker_store.util.SlugUtil;
import com.example.sneaker_store.util.exception.NameExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "CATEGORY-SERVICE")
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryEntity findById(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category!"));
    }

    @Override
    public CategoryEntity findBySlug(String slug) {
        return this.categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category!"));
    }

    @Override
    public CreateCategoryResponse createCategory(CreateCategoryRequest req) {
        if (this.categoryRepository.existsByNameAndParentId(req.getName().toUpperCase(), req.getParentId()))
            throw new NameExistsException("Name is exists");
        CategoryEntity category = new CategoryEntity();
        category.setName(req.getName().toUpperCase());
        category.setParentId(req.getParentId());

        Optional<CategoryEntity> categoryParent = this.categoryRepository.findById(req.getParentId());
        if (categoryParent.isPresent()){
            category.setSlug(SlugUtil.toSlug(req.getName()) + "-" + SlugUtil.toSlug(categoryParent.get().getName()));
        }
        else category.setSlug(SlugUtil.toSlug(req.getName()));

        this.categoryRepository.save(category);
        return this.modelMapper.map(category, CreateCategoryResponse.class);
    }

    @Override
    public UpdateCategoryResponse updateCategory(UpdateCategoryRequest req) {
        CategoryEntity category = this.findById(req.getId());
        if (this.categoryRepository.existsByNameAndIdNot(req.getName().toUpperCase(), req.getId())) {
            throw new RuntimeException("Tên category đã tồn tại!");
        }
        category.setName(req.getName().toUpperCase());
        category.setParentId(req.getParentId());

        Optional<CategoryEntity> categoryParent = this.categoryRepository.findById(req.getParentId());
        if (categoryParent.isPresent()){
            category.setSlug(SlugUtil.toSlug(req.getName()) + "-" + SlugUtil.toSlug(categoryParent.get().getName()));
        }
        else category.setSlug(SlugUtil.toSlug(req.getName()));

        this.categoryRepository.save(category);
        return this.modelMapper.map(category, UpdateCategoryResponse.class);
    }

    @Override
    public GetCategoryResponse getCategory(Pageable pageable, String name) {
        Specification<CategoryEntity> spec = CategorySpecification.specCategory(name);
        Page<CategoryEntity> page = this.categoryRepository.findAll(spec, pageable);

        GetCategoryResponse res = new GetCategoryResponse();

        GetCategoryResponse.DataPage pageRes =
                this.modelMapper.map(page, GetCategoryResponse.DataPage.class);
        res.setDataPage(pageRes);

        List<GetCategoryResponse.Category> categories = page.getContent().stream()
                .map(item -> {
                    GetCategoryResponse.Category resCate = this.modelMapper.map(item, GetCategoryResponse.Category.class);
                    Optional<CategoryEntity> categoryParent = this.categoryRepository.findById(item.getParentId());
                    if (categoryParent.isPresent()) resCate.setNameParent(categoryParent.get().getName());
                    else resCate.setNameParent("");
                    return resCate;
                })
                .toList();
        res.setCategories(categories);

        return res;
    }

    @Override
    public  List<GetCategoryResponse.Category>  getAll() {
        List<CategoryEntity> categories = this.categoryRepository.findAll();

        return categories.stream()
                .map(item -> {
                    GetCategoryResponse.Category resCate = this.modelMapper.map(item, GetCategoryResponse.Category.class);
                    Optional<CategoryEntity> categoryParent = this.categoryRepository.findById(item.getParentId());
                    if (categoryParent.isPresent()) resCate.setNameParent(categoryParent.get().getName());
                    else resCate.setNameParent("");
                    return resCate;
                })
                .toList();
    }

    @Override
    public void deleteCategory(Long id) {
        CategoryEntity category = this.findById(id);
        this.categoryRepository.deleteCategoryExistsParentId(id);
        this.categoryRepository.delete(category);
    }
}
