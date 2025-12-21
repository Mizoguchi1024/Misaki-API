package org.mizoguchi.misaki.pojo.vo.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelFrontResponse {
    private Long id;

    private String name;

    private Integer grade;

    private Integer price;

    private String path;

    private String avatarPath;

    private Boolean ownedFlag;

    private Boolean onSaleFlag;
}
