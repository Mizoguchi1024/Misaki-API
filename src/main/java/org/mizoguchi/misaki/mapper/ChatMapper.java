package org.mizoguchi.misaki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mizoguchi.misaki.pojo.entity.Chat;

import java.util.List;

public interface ChatMapper extends BaseMapper<Chat> {
    List<Chat> searchChats(Long userId, String keyword);
}
