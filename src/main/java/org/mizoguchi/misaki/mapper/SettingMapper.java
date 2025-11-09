package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.entity.Setting;

public interface SettingMapper {
    Setting selectSettingByUserId(Long userId);
    void insertSetting(Setting setting);
    void updateSetting(Setting setting);
}
