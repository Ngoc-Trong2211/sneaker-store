package com.example.sneaker_store.util;

import java.text.Normalizer;

public class SlugUtil {
    public static String toSlug(String input) {
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        slug = slug.replaceAll("đ", "d").replaceAll("Đ", "d");
        slug = slug.toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        slug = slug.trim().replaceAll("\\s+", "-");
        return slug;
    }
}
