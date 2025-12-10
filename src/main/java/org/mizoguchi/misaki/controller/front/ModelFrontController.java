package org.mizoguchi.misaki.controller.front;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "获取可购买模型")
    @GetMapping()
    @Timed("front.model.list")
    public Result<List<ModelFrontResponse>> listModels(@AuthenticationPrincipal UserDetails userDetails){
        return Result.success(modelFrontService.listModels(Long.valueOf(userDetails.getUsername())));
    }

    @Operation(summary = "购买模型")
    @PostMapping("/{id}")
    @Timed("front.model.buy")
    public Result<Void> buyModel(@AuthenticationPrincipal UserDetails userDetails, @PathVariable @Positive String id){
        modelFrontService.buyModel(Long.valueOf(userDetails.getUsername()), Long.valueOf(id));
        return Result.success();
    }




}
