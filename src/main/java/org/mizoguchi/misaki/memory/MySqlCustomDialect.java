package org.mizoguchi.misaki.memory;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MySqlCustomDialect implements JdbcChatMemoryRepositoryDialect {

    @NotNull
    @Override
    public String getSelectMessagesSql() {
        return "SELECT content, type FROM message WHERE chat_id = ? AND delete_flag = 0 ORDER BY timestamp";
    }

    @NotNull
    @Override
    public String getInsertMessageSql() {
        return "INSERT INTO message (chat_id, content, type, timestamp) VALUES(?, ?, ?, ?)";
    }

    @NotNull
    @Override
    public String getSelectConversationIdsSql() {
        return "SELECT DISTINCT chat_id AND delete_flag = 0 FROM message";
    }

    @NotNull
    @Override
    public String getDeleteMessagesSql() {
//        return "DELETE FROM message WHERE chat_id = ?";
        return "UPDATE message SET delete_flag = 1 WHERE chat_id = ?";
    }
}
