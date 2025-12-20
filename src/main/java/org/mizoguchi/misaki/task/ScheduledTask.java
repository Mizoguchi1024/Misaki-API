package org.mizoguchi.misaki.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.entity.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduledTask {
    private final UserMapper  userMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    public void executeDeleteAccount() {
        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getDeletePendingFlag, true)
                .eq(User::getDeleteFlag, false)
                .le(User::getLastLoginTime, LocalDateTime.now().minusDays(7))
                .set(User::getUsername, "Deleted account")
                .set(User::getDeletePendingFlag, false)
                .set(User::getDeleteFlag, true));
    }
}
