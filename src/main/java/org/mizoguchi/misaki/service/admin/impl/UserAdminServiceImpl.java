package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.dto.admin.AddUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateUserAdminRequest;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.admin.UserAdminResponse;
import org.mizoguchi.misaki.service.admin.UserAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {
    private final UserMapper userMapper;

    @Override
    public void addUser(AddUserAdminRequest addUserAdminRequest) {
        User user = new User();
        BeanUtils.copyProperties(addUserAdminRequest, user);
        userMapper.insert(user);
    }

    @Override
    public List<UserAdminResponse> listUsers(Integer pageIndex, Integer pageSize) {
        Page<User> page = new Page<>(pageIndex, pageSize);
        return userMapper.selectList(page, new LambdaQueryWrapper<>()).stream()
                .map(user -> {
                    UserAdminResponse userAdminResponse = new UserAdminResponse();
                    BeanUtils.copyProperties(user, userAdminResponse);

                    return userAdminResponse;
                }).collect(Collectors.toList());
    }

    @Override
    public List<UserAdminResponse> searchUsers(SearchUserAdminRequest searchUserAdminRequest) {
        // TODO
        return List.of();
    }

    @Override
    public void updateUser(Long userId, UpdateUserAdminRequest updateUserAdminRequest) {
        User user = new User();
        BeanUtils.copyProperties(updateUserAdminRequest, user);
        userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, userId));
    }

    @Override
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }
}
