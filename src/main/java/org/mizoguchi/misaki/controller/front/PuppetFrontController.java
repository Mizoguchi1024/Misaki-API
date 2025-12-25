package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.annotation.EnableRateLimit;
import org.mizoguchi.misaki.service.front.PuppetFrontService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Validated
@RestController
@RequestMapping("/front/puppet")
@RequiredArgsConstructor
@Tag(name = "用户端-助手人偶相关接口")
public class PuppetFrontController {
    private final PuppetFrontService puppetFrontService;

    @EnableRateLimit()
    @Operation(summary = "事件回应")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<String> puppetEvent(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String event) {
        return puppetFrontService.puppetEvent(Long.valueOf(userDetails.getUsername()), event);
    }
}
