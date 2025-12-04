package org.mizoguchi.misaki.service;

public interface LikesService {
    void likesMisaki(Long userId);
    void likesAssistant(Long userId, Long assistantId);
    void likesScript(Long userId, Long scriptId);
}
