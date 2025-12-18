package org.mizoguchi.misaki.service.admin;

import jakarta.validation.constraints.Positive;
import org.mizoguchi.misaki.pojo.dto.admin.SearchWishAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.WishAdminResponse;

import java.time.LocalDate;
import java.util.List;

public interface WishAdminService {
    List<WishAdminResponse> searchWishes(@Positive Integer pageIndex, @Positive Integer pageSize, String sortField, String sortOrder, SearchWishAdminRequest searchWishAdminRequest);
    void deleteWish(Long wishId);
    void deleteWishes(LocalDate date);
}
