package com.xm.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.reggie.dto.SetmealDto;
import com.xm.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void saves(SetmealDto setmealDto);

    SetmealDto getSetmealDtoById(Long id);

    Boolean removes(Long id);

    void updates(SetmealDto setmealDto);
}
