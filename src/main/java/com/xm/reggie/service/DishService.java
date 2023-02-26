package com.xm.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.reggie.common.R;
import com.xm.reggie.dto.DishDto;
import com.xm.reggie.entity.Dish;

/**
 * @author YU
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，dish和dish_Flavor表
    void saveWithFlavor(DishDto dishDto);

    DishDto getDishDtoById(Long id);

    void updateWithFlavor(DishDto dishDto);

    Boolean removeWithFlavor(Long id);
}
