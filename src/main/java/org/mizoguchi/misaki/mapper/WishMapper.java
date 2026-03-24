package org.mizoguchi.misaki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mizoguchi.misaki.pojo.entity.Wish;

public interface WishMapper extends BaseMapper<Wish> {
    Integer countFromLastHit(Long userId, Integer grade);
}
