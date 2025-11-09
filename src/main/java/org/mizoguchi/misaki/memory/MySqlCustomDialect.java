package org.mizoguchi.misaki.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MySqlCustomDialect implements JdbcChatMemoryRepositoryDialect {

    @Override
    public String getSelectMessagesSql() {
        return "SELECT content, type FROM message WHERE conversation_id = ? AND delete_flag = 0 ORDER BY timestamp";
    }

    @Override
    public String getInsertMessageSql() {
        return "INSERT INTO message (conversation_id, content, type, timestamp) VALUES(?, ?, ?, ?)";
    }

    @Override
    public String getSelectConversationIdsSql() {
        return "SELECT DISTINCT conversation_id AND delete_flag = 0 FROM message";
    }

    @Override
    public String getDeleteMessagesSql() {
//        return "DELETE FROM message WHERE conversation_id = ?";
        return "UPDATE message SET delete_flag = 1 WHERE conversation_id = ?";
    }
}
