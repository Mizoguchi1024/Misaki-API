package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.vo.front.AboutFrontResponse;

public interface LikesFrontService {
    void likeMisaki(Long userId);
    void likeAssistant(Long userId, Long assistantId);
    void likeScript(Long userId, Long scriptId);
    AboutFrontResponse getMisakiLikes(Long userId);
}
