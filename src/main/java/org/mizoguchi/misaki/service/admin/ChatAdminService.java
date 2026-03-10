package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.pojo.dto.admin.SearchChatAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateChatAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.ChatAdminResponse;

public interface ChatAdminService {
    PageResult<ChatAdminResponse> searchChats(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchChatAdminRequest searchChatAdminRequest);
    void updateChat(Long chatId, UpdateChatAdminRequest updateChatAdminRequest);
    void deleteChat(Long chatId);
}
