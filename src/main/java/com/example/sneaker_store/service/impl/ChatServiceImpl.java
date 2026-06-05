package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.ProductAiDto;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.service.ChatService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "CHAT-SERVICE")
public class ChatServiceImpl implements ChatService {

    private static final String FALLBACK_ANSWER = "Xin loi, minh chua biet cau tra loi cho cau hoi nay.";

    private final ChatClient chatClient;
    private final UserService userService;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final Map<String, String> excelData = new HashMap<>();

    public ChatServiceImpl(
            ChatClient.Builder chatBuilder,
            JdbcChatMemoryRepository chatMemoryRepository,
            UserService userService,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate
    ) {
        this.userService = userService;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();

        this.chatClient = chatBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        loadExcelData();
    }

    @Override
    public String chat(String message) {
        String excelAnswer = excelFallback(message);

        if (!excelAnswer.equals(FALLBACK_ANSWER)) {
            return excelAnswer;
        }

        String conversationId = getConversationId();

        List<ProductAiDto> products = searchProducts(message);
        String productContext = buildProductContext(products);

        try {
            return chatClient.prompt()
                    .system(buildSystemPrompt(productContext))
                    .user(message)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .call()
                    .content();

        } catch (Exception e) {
            log.warn("Chat AI unavailable: {}", summarizeAiError(e));
            return buildLocalFallbackAnswer(products);
        }
    }

    private String summarizeAiError(Exception e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            return e.getClass().getSimpleName();
        }

        int bodyStartIndex = message.indexOf(" - [");
        if (bodyStartIndex > 0) {
            return message.substring(0, bodyStartIndex);
        }

        return message.lines()
                .findFirst()
                .orElse(e.getClass().getSimpleName());
    }

    private String getConversationId() {
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);

        if (email != null && !email.equals("anonymousUser")) {
            UserEntity user = this.userService.findByEmail(email);

            if (user == null) {
                throw new IdInvalidException("Nguoi dung khong ton tai!");
            }

            return user.getId();
        }

        return "guest";
    }

    private List<ProductAiDto> searchProducts(String message) {
        String[] keywords = message.toLowerCase()
                .replaceAll("[^\\p{L}\\p{N}\\s]", "")
                .split("\\s+");

        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");

        MapSqlParameterSource params = new MapSqlParameterSource();

        int index = 0;
        for (String keyword : keywords) {
            if (keyword.isBlank()) continue;

            where.append(String.format(
                    "AND (" +
                            "LOWER(p.name) LIKE :kw%s " +
                            "OR LOWER(p.description) LIKE :kw%s " +
                            "OR LOWER(c.name) LIKE :kw%s " +
                            "OR LOWER(cp.name) LIKE :kw%s " +
                            "OR LOWER(pv.color) LIKE :kw%s" +
                            ") ",
                    index, index, index, index, index
            ));

            params.addValue("kw" + index, "%" + keyword + "%");
            index++;
        }

        String sql = sql(
                "SELECT",
                "p.id AS product_id,",
                "p.name AS product_name,",
                "p.price,",
                "p.slug,",
                "p.description,",
                "c.name AS category_name,",
                "cp.name AS parent_category_name,",
                "MIN(pv.color) AS color,",
                "MIN(pi.imageurl) AS image_url",
                "FROM tbl_product p",
                "JOIN tbl_category c ON c.id = p.category_id",
                "LEFT JOIN tbl_category cp ON cp.id = c.parent_id",
                "LEFT JOIN tbl_product_variant pv ON pv.product_id = p.id",
                "LEFT JOIN tbl_product_image pi",
                "ON pi.variant_id = pv.id",
                "AND pi.is_main = true",
                where.toString(),
                "GROUP BY",
                "p.id,",
                "p.name,",
                "p.price,",
                "p.slug,",
                "p.description,",
                "c.name,",
                "cp.name",
                "LIMIT 10"
        );

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            ProductAiDto dto = new ProductAiDto();
            dto.setProductId(rs.getString("product_id"));
            dto.setProductName(rs.getString("product_name"));
            dto.setPrice(rs.getDouble("price"));
            dto.setSlug(rs.getString("slug"));
            dto.setDescription(rs.getString("description"));
            dto.setCategoryName(rs.getString("category_name"));
            dto.setParentCategoryName(rs.getString("parent_category_name"));
            dto.setColor(rs.getString("color"));
            dto.setImageUrl(rs.getString("image_url"));
            return dto;
        });
    }

    private String buildProductContext(List<ProductAiDto> products) {
        if (products.isEmpty()) {
            return "No matching products were found in the database.";
        }

        return products.stream()
                .map(p -> String.format(
                        "- Name: %s%n" +
                                "  Price: %.0f%n" +
                                "  Slug: %s%n" +
                                "  Category: %s%n" +
                                "  Type: %s%n" +
                                "  Color: %s%n" +
                                "  Image: %s%n" +
                                "  Description: %s%n",
                        p.getProductName(),
                        p.getPrice(),
                        p.getSlug(),
                        p.getCategoryName(),
                        p.getParentCategoryName(),
                        p.getColor(),
                        p.getImageUrl(),
                        p.getDescription()
                ))
                .collect(Collectors.joining("\n"));
    }

    private String buildLocalFallbackAnswer(List<ProductAiDto> products) {
        if (products.isEmpty()) {
            return "Xin loi, he thong AI dang loi va minh chua tim thay san pham phu hop trong cua hang.";
        }

        String productLines = products.stream()
                .limit(3)
                .map(product -> String.format(
                        "- %s: %.0f VND, slug: %s",
                        product.getProductName(),
                        product.getPrice(),
                        product.getSlug()
                ))
                .collect(Collectors.joining("\n"));

        return "He thong AI dang tam thoi loi, nhung minh tim thay mot so san pham co the phu hop:\n" + productLines;
    }

    private String buildSystemPrompt(String productContext) {
        return String.format(
                "You are Sneaker Store's product assistant.%n%n" +
                        "Rules:%n" +
                        "- Only advise based on the product data below.%n" +
                        "- Do not invent products, prices, colors, or sizes.%n" +
                        "- If no product matches, say that no matching product was found.%n" +
                        "- If the customer asks about login or register, guide them to /login or /register.%n" +
                        "- Reply in Vietnamese when the customer writes Vietnamese.%n" +
                        "- Reply in English when the customer writes English.%n" +
                        "- When recommending products, include the product name, price, and slug.%n%n" +
                        "Product data:%n%s",
                productContext
        );
    }

    private String excelFallback(String userMessage) {
        String key = userMessage.trim().toLowerCase();
        return excelData.getOrDefault(key, FALLBACK_ANSWER);
    }

    private void loadExcelData() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("ChatSneaker.xlsx")) {
            if (is == null) {
                log.warn("Cannot find ChatSneaker.xlsx");
                return;
            }

            try (Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    Cell questionCell = row.getCell(0);
                    Cell answerCell = row.getCell(1);

                    if (questionCell != null && answerCell != null) {
                        excelData.put(
                                questionCell.getStringCellValue().trim().toLowerCase(),
                                answerCell.getStringCellValue().trim()
                        );
                    }
                }
            }

        } catch (IOException e) {
            log.error("Cannot read ChatSneaker.xlsx", e);
        }
    }

    private static String sql(String... lines) {
        return String.join(" ", lines);
    }
}
