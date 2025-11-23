package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.entity.User;

public interface UserMapper {
    User selectUserById(Long id);
    User selectUserByEmail(String email);
    void insertUser(User user);
    void updateUserById(User user);
}
