package org.mizoguchi.misaki.service.front.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.service.front.PuppetFrontService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class PuppetFrontServiceImpl implements PuppetFrontService {
    private final UserMapper userMapper;
    private final AssistantMapper assistantMapper;

    @Override
    public Flux<String> puppetEvent(Long userId, String event) {
        return null;
    }
}
