package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.vo.front.ModelFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.WishFrontResponse;

import java.util.List;

public interface WishService {
    void buyPuzzle(Long userId, Integer amount);
    void buyModel(Long userId, Long modelId);
    WishFrontResponse gacha(Long userId, Integer amount);
    List<ModelFrontResponse> listModelFrontResponse(Long userId);
    List<WishFrontResponse> listWishFrontResponse(Long userId);
}
