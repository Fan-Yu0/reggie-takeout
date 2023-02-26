package com.xm.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.reggie.common.R;
import com.xm.reggie.dto.DishDto;
import com.xm.reggie.dto.SetmealDto;
import com.xm.reggie.entity.Category;
import com.xm.reggie.entity.Dish;
import com.xm.reggie.entity.Setmeal;
import com.xm.reggie.service.CategoryService;
import com.xm.reggie.service.SetmealDishesService;
import com.xm.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishesService setmealDishesService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saves(setmealDto);
        return R.success("新增成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);

        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = records.stream().map(item->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);

            Long id = item.getCategoryId();

            Category category = categoryService.getById(id);

            if(category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable long id){
        SetmealDto setmeal = setmealService.getSetmealDtoById(id);
        return R.success(setmeal);
    }


    /**
     * 根据id删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> remove(Long[] ids){
        for(Long id : ids){
            if(!setmealService.removes(id)){
                return R.error("删除失败");
            }
        }

        return R.success("删除成功");
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updates(setmealDto);
        return R.success("新增成功");
    }


    /**
     * 批量停用
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> disable(Long[] ids){
        for(Long id : ids){
            Setmeal setmeal = setmealService.getById(id);
            if(setmeal.getStatus() == 1) {
                setmeal.setStatus(0);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("禁用成功");
    }

    /**
     * 批量启用
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> enable(Long[] ids){
        for(Long id : ids){
            Setmeal setmeal = setmealService.getById(id);
            if(setmeal.getStatus() == 0) {
                setmeal.setStatus(1);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("启用成功");
    }



    /**
     * 查询所有套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(wrapper);


        return R.success(list);
    }



}
