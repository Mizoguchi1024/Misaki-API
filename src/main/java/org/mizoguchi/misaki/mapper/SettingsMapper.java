package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.pojo.entity.Settings;

public interface SettingsMapper {
    Settings selectSettingsByUserId(Long userId);
    void insertSettings(Settings settings);
    void updateSettingsByUserId(Settings settings);
}
