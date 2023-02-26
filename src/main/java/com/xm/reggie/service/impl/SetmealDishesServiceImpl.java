package com.xm.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.reggie.entity.SetmealDish;
import com.xm.reggie.mapper.SetmealDishesMapper;
import com.xm.reggie.service.SetmealDishesService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishesServiceImpl extends ServiceImpl<SetmealDishesMapper, SetmealDish> implements SetmealDishesService {
}
