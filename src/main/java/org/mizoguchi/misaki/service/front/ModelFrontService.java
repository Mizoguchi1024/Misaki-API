package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.vo.front.ModelFrontResponse;

import java.util.List;

public interface ModelFrontService {
    List<ModelFrontResponse> listModels(Long userId);
    void buyModel(Long userId, Long modelId);
}
