package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.vo.front.ModelFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.WishFrontResponse;

import java.util.List;

public interface WishFrontService {
    WishFrontResponse gacha(Long userId, Integer amount);
    List<ModelFrontResponse> listModels(Long userId);
    List<WishFrontResponse> listWishes(Long userId);
    void buyPuzzleWithCrystal(Long userId, Integer amount);
    void buyPuzzleWithStardust(Long userId, Integer amount);
    void buyModel(Long userId, Long modelId);
}
