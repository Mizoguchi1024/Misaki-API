package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.vo.front.WishFrontResponse;

import java.util.List;

public interface WishFrontService {
    List<WishFrontResponse> gacha(Long userId, Integer times);
    List<WishFrontResponse> listWishes(Long userId);
    void buyPuzzleWithCrystal(Long userId, Integer amount);
    void buyPuzzleWithStardust(Long userId, Integer amount);
}
