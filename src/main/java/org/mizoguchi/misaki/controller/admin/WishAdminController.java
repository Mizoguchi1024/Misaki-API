package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/admin/wish")
@RequiredArgsConstructor
@Tag(name = "管理端-祈愿相关接口")
public class WishAdminController {

}
