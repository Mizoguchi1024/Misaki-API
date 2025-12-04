package org.mizoguchi.misaki.pojo.vo.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishFrontResponse {
    private Integer prize;

    private Integer amount;
}
