package com.example.sneaker_store.util;

public class SkuGenerator {
    public static String generate(String brand, String productName, String color, String size) {
        String brandCode = toCode(brand);
        String productCode = toCode(productName);
        String colorCode = toCode(color);

        return String.format("%s-%s-%s-%s",
                brandCode,
                productCode,
                colorCode,
                size.toUpperCase()
        );
    }

    private static String toCode(String input) {
        return input.trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "")
                .substring(0, Math.min(3, input.length()));
    }
}
