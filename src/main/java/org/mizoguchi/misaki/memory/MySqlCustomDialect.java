package org.mizoguchi.misaki.memory;

import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.stereotype.Component;

@Component
@NonNullApi
@RequiredArgsConstructor
public class MySqlCustomDialect implements JdbcChatMemoryRepositoryDialect {
    @Override
    public String getSelectMessagesSql() {
        return "SELECT content, type FROM message WHERE chat_id = ? AND delete_flag = 0 ORDER BY timestamp";
    }

    @Override
    public String getInsertMessageSql() {
        return "INSERT INTO message (chat_id, content, type, timestamp) VALUES(?, ?, ?, ?)";
    }

    @Override
    public String getSelectConversationIdsSql() {
        return "SELECT DISTINCT chat_id AND delete_flag = 0 FROM message";
    }

    @Override
    public String getDeleteMessagesSql() {
        return "DELETE FROM message WHERE chat_id = ?";
    }
}
