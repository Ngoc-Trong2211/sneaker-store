package com.example.sneaker_store.dto.request.productVariant;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateProductVariantRequest {
    @NotBlank(message = "ID biến thể sản phẩm không được để trống")
    private String id;

    @NotBlank(message = "Màu sắc không được để trống")
    private String color;

    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    private String productName;

    @Size(max = 6, message = "Chỉ được phép tải lên tối đa 6 ảnh")
    private List<String> images;

    private List<SizeRequest> sizes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SizeRequest {
        @NotNull(message = "ID là bắt buộc")
        private Long id;

        @NotBlank(message = "Kích cỡ là bắt buộc")
        private String size;

        @NotNull(message = "Số lượng là bắt buộc")
        private Integer quantity;
    }
}
