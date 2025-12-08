package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.pojo.dto.admin.AddUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateUserAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.UserAdminResponse;

import java.util.List;

public interface UserAdminService {
    List<UserAdminResponse> listUsers(Integer pageIndex, Integer pageSize);
    void addUser(AddUserAdminRequest addUserAdminRequest);
    void updateUser(Long userId, UpdateUserAdminRequest updateUserAdminRequest);
    void deleteUser(Long userId);
}
