package com.example.sneaker_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FavouriteResponse {
    private String productId;
    private String url;
    private String productName;
    private List<Variant> variants;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Variant{
        private String color;
        private String url;
    }
}
