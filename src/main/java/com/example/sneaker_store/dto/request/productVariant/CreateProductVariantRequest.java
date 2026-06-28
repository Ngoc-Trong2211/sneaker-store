package com.example.sneaker_store.dto.request.productVariant;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateProductVariantRequest {
    @NotBlank(message = "Màu sắc là bắt buộc")
    private String color;

    @NotBlank(message = "ID sản phẩm là bắt buộc")
    private String productName;

    @Size(max = 6, message = "Chỉ được phép tải lên tối đa 6 ảnh")
    private List<String> images;

    @NotEmpty(message = "Danh sách kích cỡ là bắt buộc")
    private List<SizeRequest> sizes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SizeRequest {

        @NotBlank(message = "Kích cỡ là bắt buộc")
        private String size;

        @NotNull(message = "Số lượng là bắt buộc")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity;
    }
}
