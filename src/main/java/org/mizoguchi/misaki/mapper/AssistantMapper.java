package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.pojo.entity.Assistant;

import java.util.List;

public interface AssistantMapper {
    Assistant selectAssistantById(Long id);
    List<Assistant> selectAssistantsByOwnerId(Long ownerId);
    Void insertAssistant(Assistant assistant);
    Void updateAssistantById(Assistant assistant);
}
