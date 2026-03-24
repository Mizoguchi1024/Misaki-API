package org.mizoguchi.misaki.common.result;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;
    private Integer total;
    private Integer pageIndex;
    private Integer pageSize;
}
