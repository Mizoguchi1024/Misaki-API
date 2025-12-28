package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.SqlConstant;
import org.mizoguchi.misaki.common.exception.OptimisticLockFailedException;
import org.mizoguchi.misaki.common.exception.UserNotExistsException;
import org.mizoguchi.misaki.mapper.*;
import org.mizoguchi.misaki.pojo.dto.admin.AddUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateUserAdminRequest;
import org.mizoguchi.misaki.pojo.entity.*;
import org.mizoguchi.misaki.pojo.vo.admin.UserAdminResponse;
import org.mizoguchi.misaki.service.admin.UserAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SettingsMapper settingsMapper;
    private final AssistantMapper assistantMapper;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final ModelUserMapper modelUserMapper;
    private final WishMapper wishMapper;

    @Override
    public void addUser(AddUserAdminRequest addUserAdminRequest) {
        User user = new User();
        BeanUtils.copyProperties(addUserAdminRequest, user);
        String encryptPassword = passwordEncoder.encode(addUserAdminRequest.getPassword());
        user.setPassword(encryptPassword);
        user.setToken(10000);
        user.setCrystal(0);
        user.setStardust(0);
        user.setStardust(0);

        userMapper.insert(user);
        // TODO 其他表的插入
    }

    @Override
    public List<UserAdminResponse> searchUsers(Integer pageIndex, Integer pageSize, String sortField, String sortOrder,
                                               SearchUserAdminRequest searchUserAdminRequest) {
        List<User> users = userMapper.selectList(new Page<>(pageIndex, pageSize), new QueryWrapper<User>()
                        .orderBy(sortField != null, sortOrder.equalsIgnoreCase(SqlConstant.ASC), sortField)
                        .lambda()
                        .like(searchUserAdminRequest.getId() != null, User::getId, searchUserAdminRequest.getId())
                        .eq(searchUserAdminRequest.getAuthRole() != null, User::getAuthRole, searchUserAdminRequest.getAuthRole())
                        .like(searchUserAdminRequest.getEmail() != null, User::getEmail, searchUserAdminRequest.getEmail())
                        .like(searchUserAdminRequest.getUsername() != null, User::getUsername, searchUserAdminRequest.getUsername())
                        .eq(searchUserAdminRequest.getGender() != null, User::getGender, searchUserAdminRequest.getGender())
                        .eq(searchUserAdminRequest.getBirthday() != null, User::getBirthday, searchUserAdminRequest.getBirthday())
                        .like(searchUserAdminRequest.getOccupation() != null, User::getOccupation, searchUserAdminRequest.getOccupation())
                        .like(searchUserAdminRequest.getDetail() != null, User::getDetail, searchUserAdminRequest.getDetail())
                        .eq(searchUserAdminRequest.getLastCheckInDate() != null, User::getLastCheckInDate, searchUserAdminRequest.getLastCheckInDate())
                        .like(searchUserAdminRequest.getLastLoginTime() != null, User::getLastLoginTime, searchUserAdminRequest.getLastLoginTime())
                        .eq(searchUserAdminRequest.getDeletePendingFlag() != null, User::getDeletePendingFlag, searchUserAdminRequest.getDeletePendingFlag())
                        .eq(searchUserAdminRequest.getDeleteFlag() != null, User::getDeleteFlag, searchUserAdminRequest.getDeleteFlag())
                        .like(searchUserAdminRequest.getCreateTime() != null, User::getCreateTime, searchUserAdminRequest.getCreateTime())
                        .like(searchUserAdminRequest.getUpdateTime() != null, User::getUpdateTime, searchUserAdminRequest.getUpdateTime())
        );

        return users.stream()
                .map(user -> {
                    UserAdminResponse userAdminResponse = new UserAdminResponse();
                    BeanUtils.copyProperties(user, userAdminResponse);

                    return userAdminResponse;
                }).collect(Collectors.toList());
    }

    @Override
    public void updateUser(Long userId, UpdateUserAdminRequest updateUserAdminRequest) {
        boolean existsFlag = userMapper.exists(new LambdaQueryWrapper<User>()
                .eq(User::getId, userId)
        );

        if (!existsFlag) {
            throw new UserNotExistsException(FailMessageConstant.USER_NOT_EXISTS);
        }

        User user = new User();
        BeanUtils.copyProperties(updateUserAdminRequest, user);
        user.setId(userId);
        int affectedRows = userMapper.updateById(user);

        if (affectedRows == 0) {
            throw new OptimisticLockFailedException(FailMessageConstant.OPTIMISTIC_LOCK_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        settingsMapper.delete(new LambdaQueryWrapper<Settings>().eq(Settings::getUserId, userId));
        assistantMapper.delete(new LambdaQueryWrapper<Assistant>().eq(Assistant::getOwnerId, userId));
        modelUserMapper.delete(new LambdaQueryWrapper<ModelUser>().eq(ModelUser::getUserId, userId));
        wishMapper.delete(new LambdaQueryWrapper<Wish>().eq(Wish::getUserId, userId));

        chatMapper.selectList(new LambdaQueryWrapper<Chat>().eq(Chat::getUserId, userId)).forEach(chat ->
                messageMapper.delete(new LambdaQueryWrapper<Message>().eq(Message::getChatId, chat.getId())));
        chatMapper.delete(new LambdaQueryWrapper<Chat>().eq(Chat::getUserId, userId));
        int affectedRows = userMapper.deleteById(userId);

        if (affectedRows == 0) {
            throw new UserNotExistsException(FailMessageConstant.USER_NOT_EXISTS);
        }
    }
}
