package org.mizoguchi.misaki.service.front;

import reactor.core.publisher.Flux;

public interface PuppetFrontService {
    Flux<String> puppetEvent(Long userId, String event);
}
