package com.xm.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.reggie.common.R;
import com.xm.reggie.dto.DishDto;
import com.xm.reggie.entity.Dish;
import com.xm.reggie.entity.DishFlavor;
import com.xm.reggie.mapper.DishMapper;
import com.xm.reggie.service.DishFlavorService;
import com.xm.reggie.service.DishService;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存菜品对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish表
        this.save(dishDto);

        Long dishId = dishDto.getId();
        //保存菜品对应的口味数据到dish_flavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 根据菜品ID查询菜品信息
     * @param id
     * @return
     */
    @Override
    public DishDto getDishDtoById(Long id) {
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavors);


        return dishDto;
    }


    /**
     * 更新菜品，同时更新菜品对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品的基本信息到dish表
        this.updateById(dishDto);

        Long dishId = dishDto.getId();
        //删除菜品对应的口味数据到dish_flavor表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(queryWrapper);

        //保存菜品对应的口味数据到dish_flavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 删除菜品，同时删除菜品对应的口味数据
     * @param id
     */
    @Override
    public Boolean removeWithFlavor(Long id) {
        Dish dish = this.getById(id);
        if(dish.getStatus() != 0){
            return false;
        }

        //删除菜品的基本信息到dish表
        this.removeById(id);

        //删除菜品对应的口味数据到dish_flavor表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(queryWrapper);
        return true;
    }
}
