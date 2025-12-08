package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.pojo.dto.admin.AddUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateUserAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.UserAdminResponse;

import java.util.List;

public interface UserAdminService {
    void addUser(AddUserAdminRequest addUserAdminRequest);
    List<UserAdminResponse> searchUsers(SearchUserAdminRequest searchUserAdminRequest);
    List<UserAdminResponse> listUsers(Integer pageIndex, Integer pageSize);
    void updateUser(Long userId, UpdateUserAdminRequest updateUserAdminRequest);
    void deleteUser(Long userId);
}
