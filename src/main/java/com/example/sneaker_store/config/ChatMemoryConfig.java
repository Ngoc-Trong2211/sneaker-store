package com.example.sneaker_store.config;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class ChatMemoryConfig {

    @Bean
    public JdbcChatMemoryRepository jdbcChatMemoryRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(JdbcChatMemoryRepositoryDialect.from(dataSource))
                .build();
    }

    @Bean
    public ApplicationRunner chatMemorySchemaInitializer(DataSource dataSource) {
        return args -> {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                    new ClassPathResource("org/springframework/ai/chat/memory/repository/jdbc/schema-mysql.sql")
            );
            populator.setContinueOnError(true);
            DatabasePopulatorUtils.execute(populator, dataSource);
        };
    }
}
