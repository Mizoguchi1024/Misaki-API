package org.mizoguchi.misaki.service;

public interface LikeService {
    void likeMisaki(Long userId);
    void likeAssistant(Long userId, Long assistantId);
    void likeScript(Long userId, Long scriptId);
}
