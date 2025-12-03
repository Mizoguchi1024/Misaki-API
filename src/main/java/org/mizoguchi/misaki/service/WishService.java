package org.mizoguchi.misaki.service;

public interface WishService {
    void buyPuzzle(Long userId, Integer amount);
    void buyModel(Long userId, Long modelId);
    void wish(Long userId, Integer amount);
}
