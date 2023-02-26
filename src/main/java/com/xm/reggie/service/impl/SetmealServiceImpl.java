package com.xm.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.reggie.dto.SetmealDto;
import com.xm.reggie.entity.Setmeal;
import com.xm.reggie.entity.SetmealDish;
import com.xm.reggie.mapper.SetmeaMapper;
import com.xm.reggie.service.SetmealDishesService;
import com.xm.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmeaMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishesService setmealDishesService;



    @Override
    @Transactional
    public void saves(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        setmealDishesService.saveBatch(setmealDishes);

    }

    @Override
    public SetmealDto getSetmealDtoById(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaUpdateWrapper<SetmealDish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SetmealDish::getId,setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishesService.list(lambdaUpdateWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    @Override

    public Boolean removes(Long id) {
        Setmeal setmeal = this.getById(id);
        if(setmeal.getStatus() != 0){
            return false;
        }

        this.removeById(id);

        LambdaUpdateWrapper<SetmealDish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishesService.remove(lambdaUpdateWrapper);
        return true;

    }

    @Override
    public void updates(SetmealDto setmealDto) {

        this.updateById(setmealDto);

        Long id = setmealDto.getId();
        LambdaUpdateWrapper<SetmealDish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishesService.remove(lambdaUpdateWrapper);


        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map(item->{
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());

        setmealDishesService.saveBatch(setmealDishes);

    }
}
