package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.vo.front.WishFrontResponse;

public interface WishService {
    void buyPuzzle(Long userId, Integer amount);
    void buyModel(Long userId, Long modelId);
    WishFrontResponse wish(Long userId, Integer amount);
}
