package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.pojo.dto.admin.AddUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateUserAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.UserAdminResponse;

public interface UserAdminService {
    void addUser(AddUserAdminRequest addUserAdminRequest);
    PageResult<UserAdminResponse> searchUsers(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchUserAdminRequest searchUserAdminRequest);
    void updateUser(Long userId, UpdateUserAdminRequest updateUserAdminRequest);
    void deleteUser(Long userId);
}
