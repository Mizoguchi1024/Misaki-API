package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.dto.front.SendMessageFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.McpServerFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.MessageFrontResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MessageFrontService {
    Flux<String> sendMessage(Long userId, Long chatId, SendMessageFrontRequest sendMessageFrontRequest);
    List<MessageFrontResponse> listMessages(Long userId, Long chatId);
    List<McpServerFrontResponse> listMcpServers();
}
