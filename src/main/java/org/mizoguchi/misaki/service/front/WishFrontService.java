package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.vo.front.WishFrontResponse;

import java.util.List;

public interface WishFrontService {
    WishFrontResponse gacha(Long userId, Integer amount);
    List<WishFrontResponse> listWishes(Long userId);
    void buyPuzzleWithCrystal(Long userId, Integer amount);
    void buyPuzzleWithStardust(Long userId, Integer amount);
}
