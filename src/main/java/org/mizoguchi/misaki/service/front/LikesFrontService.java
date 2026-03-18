package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.vo.front.AboutFrontResponse;

public interface LikesFrontService {
    void likeObject(Long userId, Integer targetType, Long targetId);
    AboutFrontResponse getMisakiLikes(Long userId);
}
