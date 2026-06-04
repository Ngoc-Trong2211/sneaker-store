package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.ProductAiDto;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.sneaker_store.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "CHAT-SERVICE")
public class ChatServiceImpl implements ChatService {

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

        if (!excelAnswer.equals("Xin lỗi, mình chưa biết câu trả lời cho câu hỏi này.")) {
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
            log.error("Chat AI error", e);
            return "Xin lỗi, hệ thống AI đang lỗi. Bạn vui lòng thử lại sau.";
        }
    }

    private String getConversationId() {
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);

        if (email != null && !email.equals("anonymousUser")) {
            UserEntity user = this.userService.findByEmail(email);

            if (user == null) {
                throw new IdInvalidException("Người dùng không tồn tại!");
            }

            return "conversation-" + user.getId();
        }

        return "conversation-guest";
    }

    private List<ProductAiDto> searchProducts(String message) {
        String[] keywords = message.toLowerCase()
                .replaceAll("[^a-zA-Z0-9À-ỹ\\s]", "")
                .split("\\s+");

        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");

        MapSqlParameterSource params = new MapSqlParameterSource();

        int index = 0;
        for (String keyword : keywords) {
            if (keyword.isBlank()) continue;

            where.append("""
            AND (
                LOWER(p.name) LIKE :kw%s
                OR LOWER(p.description) LIKE :kw%s
                OR LOWER(c.name) LIKE :kw%s
                OR LOWER(cp.name) LIKE :kw%s
                OR LOWER(pv.color) LIKE :kw%s
            )
            """.formatted(index, index, index, index, index));

            params.addValue("kw" + index, "%" + keyword + "%");
            index++;
        }

        String sql = """
        SELECT
            p.id AS product_id,
            p.name AS product_name,
            p.price,
            p.slug,
            p.description,
            c.name AS category_name,
            cp.name AS parent_category_name,
            MIN(pv.color) AS color,
            MIN(pi.imageurl) AS image_url
        FROM tbl_product p
        JOIN tbl_category c ON c.id = p.category_id
        LEFT JOIN tbl_category cp ON cp.id = c.parent_id
        LEFT JOIN tbl_product_variant pv ON pv.product_id = p.id
        LEFT JOIN tbl_product_image pi
            ON pi.variant_id = pv.id
            AND pi.is_main = true
        """ + where + """
        GROUP BY
            p.id,
            p.name,
            p.price,
            p.slug,
            p.description,
            c.name,
            cp.name
        LIMIT 10
        """;

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
            return "Không tìm thấy sản phẩm phù hợp trong database.";
        }

        return products.stream()
                .map(p -> """
                    - Tên: %s
                      Giá: %.0f
                      Slug: %s
                      Danh mục: %s
                      Loại: %s
                      Màu: %s
                      Ảnh: %s
                      Mô tả: %s
                    """.formatted(
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

    private String buildSystemPrompt(String productContext) {
        return """
            Bạn là trợ lý tư vấn của Sneaker Store.

            Quy tắc:
            - Chỉ tư vấn dựa trên dữ liệu sản phẩm bên dưới.
            - Không tự bịa sản phẩm, giá, màu, size.
            - Nếu không có sản phẩm phù hợp, hãy nói chưa tìm thấy sản phẩm phù hợp.
            - Nếu khách hỏi đăng nhập hoặc đăng ký, hướng dẫn vào /login hoặc /register.
            - Nếu khách viết tiếng Việt, trả lời tiếng Việt.
            - Nếu khách viết tiếng Anh, trả lời tiếng Anh.
            - Khi gợi ý sản phẩm, hãy nói tên sản phẩm, giá và slug.

            Dữ liệu sản phẩm:
            %s
            """.formatted(productContext);
    }

    private String excelFallback(String userMessage) {
        String key = userMessage.trim().toLowerCase();
        return excelData.getOrDefault(key, "Xin lỗi, mình chưa biết câu trả lời cho câu hỏi này.");
    }

    private void loadExcelData() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("ChatSneaker.xlsx")) {
            if (is == null) {
                log.warn("Không tìm thấy file Excel ChatSneaker.xlsx");
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
            log.error("Lỗi đọc file Excel ChatSneaker.xlsx", e);
        }
    }
}
