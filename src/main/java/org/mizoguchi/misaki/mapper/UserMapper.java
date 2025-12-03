package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.pojo.entity.User;

public interface UserMapper {
    void insertUser(User user);
    User selectUserById(Long id);
    User selectUserByEmail(String email);
    void updateUserById(User user);
}
