package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;

@Data
public class SearchModelAdminRequest {
    private Long id;

    private String name;

    private Integer grade;

    private Integer price;

    private String path;

    private String avatarPath;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate createTime;
}
