package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.enumeration.AuthRoleEnum;
import org.mizoguchi.misaki.common.exception.UserNotExistsException;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) {
        User user = userMapper.selectUserById(Long.valueOf(userId));

        if (user == null) {
            throw new UserNotExistsException(MessageConstant.USER_NOT_EXISTS);
        }

        AuthRoleEnum authRoleEnum = AuthRoleEnum.fromCode(user.getAuthRole());

        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(authRoleEnum.getRoleName()))
        );
    }
}
