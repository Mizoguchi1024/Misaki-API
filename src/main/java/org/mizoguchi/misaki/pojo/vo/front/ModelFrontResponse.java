package org.mizoguchi.misaki.pojo.vo.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelFrontResponse {
    private String name;

    private Integer grade;

    private Integer price;

    private String path;

    private String avatarPath;
}
