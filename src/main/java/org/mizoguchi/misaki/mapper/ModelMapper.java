package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.pojo.entity.Model;

public interface ModelMapper {
    void insertModel(Model model);
    Model selectModelById(Long id);
    void updateModel(Model model);
    void deleteModelById(Long id);
}
