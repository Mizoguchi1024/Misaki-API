package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.ChatNotExistsException;
import org.mizoguchi.misaki.common.exception.OptimisticLockFailedException;
import org.mizoguchi.misaki.mapper.ChatMapper;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.mizoguchi.misaki.pojo.dto.admin.SearchChatAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateChatAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Chat;
import org.mizoguchi.misaki.pojo.entity.Message;
import org.mizoguchi.misaki.pojo.vo.admin.ChatAdminResponse;
import org.mizoguchi.misaki.service.admin.ChatAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatAdminServiceImpl implements ChatAdminService {
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    @Override
    public List<ChatAdminResponse> searchChats(Integer pageIndex, Integer pageSize, String sortField, String sortOrder,
                                               SearchChatAdminRequest searchChatAdminRequest) {
        List<Chat> chats = chatMapper.selectList(new Page<>(pageIndex, pageSize), new QueryWrapper<Chat>()
                .orderBy(sortField != null, sortOrder.equalsIgnoreCase("asc"), sortField)
                .lambda()
                .like(searchChatAdminRequest.getId() != null, Chat::getId, searchChatAdminRequest.getId())
                .like(searchChatAdminRequest.getUserId() != null, Chat::getUserId, searchChatAdminRequest.getUserId())
                .like(searchChatAdminRequest.getTitle() != null, Chat::getTitle, searchChatAdminRequest.getTitle())
                .like(searchChatAdminRequest.getDeleteFlag() != null, Chat::getDeleteFlag, searchChatAdminRequest.getDeleteFlag())
                .like(searchChatAdminRequest.getCreateTime() != null, Chat::getCreateTime, searchChatAdminRequest.getCreateTime())
                .like(searchChatAdminRequest.getUpdateTime() != null, Chat::getUpdateTime, searchChatAdminRequest.getUpdateTime())
        );

        return chats.stream()
                .map(chat -> {
                    ChatAdminResponse chatAdminResponse = new ChatAdminResponse();
                    BeanUtils.copyProperties(chat, chatAdminResponse);

                    return chatAdminResponse;
                }).collect(Collectors.toList());
    }

    @Override
    public void updateChat(Long chatId, UpdateChatAdminRequest updateChatAdminRequest) {
        boolean existsFlag = chatMapper.exists(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
        );

        if (!existsFlag) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        Chat chat = new Chat();
        BeanUtils.copyProperties(updateChatAdminRequest, chat);
        chat.setId(chatId);
        int affectedRows = chatMapper.updateById(chat);

        if (affectedRows == 0) {
            throw new OptimisticLockFailedException(FailMessageConstant.OPTIMISTIC_LOCK_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteChat(Long chatId) {
        messageMapper.delete(new LambdaQueryWrapper<Message>()
                .eq(Message::getChatId, chatId));
        int affectedRows = chatMapper.deleteById(chatId);

        if (affectedRows == 0) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }
    }
}
