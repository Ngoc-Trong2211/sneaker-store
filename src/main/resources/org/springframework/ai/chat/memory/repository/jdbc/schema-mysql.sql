-- Spring AI JdbcChatMemoryRepository (MySQL). Must live at this classpath path for the JDBC memory auto-config.
CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
     conversation_id VARCHAR(64) NOT NULL,
     content MEDIUMTEXT NOT NULL,
     type VARCHAR(100) NOT NULL,
     `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     INDEX SPRING_AI_CHAT_MEMORY_CONVERSATION_ID_TIMESTAMP_IDX (conversation_id, `timestamp`)
);

ALTER TABLE SPRING_AI_CHAT_MEMORY
    MODIFY conversation_id VARCHAR(64) NOT NULL;

ALTER TABLE SPRING_AI_CHAT_MEMORY
    MODIFY content MEDIUMTEXT NOT NULL;
