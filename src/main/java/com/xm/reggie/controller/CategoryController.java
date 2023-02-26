package com.xm.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.reggie.common.R;
import com.xm.reggie.entity.Category;
import com.xm.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page<Category> categoryPage = new Page<>(page, pageSize);

        LambdaUpdateWrapper<Category> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(categoryPage);

        return R.success(categoryPage);

    }

    /***
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){

        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //构建查询条件
        LambdaUpdateWrapper<Category> queryWrapper = new LambdaUpdateWrapper<>();
        //根据分类名称模糊查询
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //排序规则
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
