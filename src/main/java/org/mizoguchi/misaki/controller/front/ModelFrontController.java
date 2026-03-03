package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.annotation.EnableRateLimit;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.vo.front.ModelFrontResponse;
import org.mizoguchi.misaki.service.front.ModelFrontService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/front/models")
@RequiredArgsConstructor
@Tag(name = "用户端-模型相关接口")
public class ModelFrontController {
    private final ModelFrontService modelFrontService;

    @EnableRateLimit()
    @Operation(summary = "获取拥有的模型")
    @GetMapping()
    public Result<List<ModelFrontResponse>> listModels(@AuthenticationPrincipal UserDetails userDetails){
        return Result.success(modelFrontService.listModels(Long.valueOf(userDetails.getUsername())));
    }

    @EnableRateLimit()
    @Operation(summary = "购买模型")
    @PostMapping("/{id}")
    public Result<Void> buyModel(@AuthenticationPrincipal UserDetails userDetails, @PathVariable @Positive Long id){
        modelFrontService.buyModel(Long.valueOf(userDetails.getUsername()), id);
        return Result.success();
    }
}
