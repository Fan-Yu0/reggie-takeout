package com.xm.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.reggie.common.CustomException;
import com.xm.reggie.entity.Category;
import com.xm.reggie.entity.Dish;
import com.xm.reggie.entity.Setmeal;
import com.xm.reggie.mapper.CategoryMapper;
import com.xm.reggie.service.CategoryService;
import com.xm.reggie.service.DishService;
import com.xm.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void remove(Long id) {

        //查询分类下是否关联了菜品，如果有则不允许删除，抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1 > 0){
            throw new CustomException("该分类下关联了菜品，不允许删除");
        }
        //查询分类下是否关联了套餐，如果有则不允许删除，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2 > 0){
            throw new CustomException("该分类下关联了套餐，不允许删除");
        }

        //删除分类
        super.removeById(id);


    }
}
