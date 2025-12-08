package org.mizoguchi.misaki.service.front;

public interface LikesFrontService {
    void likeMisaki(Long userId);
    void likeAssistant(Long userId, Long assistantId);
    void likeScript(Long userId, Long scriptId);
}
