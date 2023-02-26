package com.xm.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.reggie.common.R;
import com.xm.reggie.dto.DishDto;
import com.xm.reggie.entity.Category;
import com.xm.reggie.entity.Dish;
import com.xm.reggie.entity.DishFlavor;
import com.xm.reggie.service.CategoryService;
import com.xm.reggie.service.DishFlavorService;
import com.xm.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 保存菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
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
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPageInfo = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPageInfo,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            //获取菜品对应的分类ID
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPageInfo.setRecords(list);


        return R.success(dishDtoPageInfo);
    }


    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getDishDtoById(id);
        return R.success(dishDto);
    }

    /**
     * 保存菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");

    }

    /**
     * 批量停用
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> disable(Long[] ids){
        for(Long id : ids){
            Dish dish = dishService.getById(id);
            if(dish.getStatus() == 1) {
                dish.setStatus(0);
                dishService.updateById(dish);
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
            Dish dish = dishService.getById(id);
            if(dish.getStatus() == 0) {
                dish.setStatus(1);
                dishService.updateById(dish);
            }
        }
        return R.success("启用成功");
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> remove(Long[] ids){
        for(Long id : ids){
           if(!dishService.removeWithFlavor(id)){
               return R.error("删除失败");
           }
        }
        return R.success("删除成功");

    }


//    /**
//     * 查询菜系中的所有菜品
//     * @param dish
//     * @return
//     */
//
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//
//        queryWrapper.eq(Dish::getStatus,1);  //只查询启用的菜品
//        //排序
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//
//    }


    /**
     * 查询菜系中的所有菜品
     * @param dish
     * @return
     */

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());

        //只查询启用的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);


        List<DishDto> dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            //获取菜品对应的分类ID
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //获取菜品对应的口味
            Long id = item.getId();

            //sql : select * from dish_flavor where dish_id = #{id}
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);


            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);

    }

}
