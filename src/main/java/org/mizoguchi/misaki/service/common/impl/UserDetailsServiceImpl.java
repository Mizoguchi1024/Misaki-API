package org.mizoguchi.misaki.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.enumeration.AuthRoleEnum;
import org.mizoguchi.misaki.common.exception.UserNotExistsException;
import org.mizoguchi.misaki.config.security.CustomUserDetails;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getId, userId)
                .eq(User::getDeleteFlag, false));

        if (user == null) {
            throw new UserNotExistsException(FailMessageConstant.USER_NOT_EXISTS);
        }

        AuthRoleEnum authRoleEnum = AuthRoleEnum.fromCode(user.getAuthRole());

        return CustomUserDetails.builder()
                .username(userId)
                .password(user.getPassword())
                .authorities(Set.of(new SimpleGrantedAuthority(authRoleEnum.getRoleWithPrefix())))
                .build();
    }
}
