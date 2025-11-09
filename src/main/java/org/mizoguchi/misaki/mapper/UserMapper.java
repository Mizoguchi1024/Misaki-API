package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.entity.User;

public interface UserMapper {
    User selectUserByEmail(String email);
    void insertUser(User user);
    void updateUser(User user);
}
