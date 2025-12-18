package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/admin/models")
@RequiredArgsConstructor
@Tag(name = "管理端-模型相关接口")
public class ModelAdminController {
}
