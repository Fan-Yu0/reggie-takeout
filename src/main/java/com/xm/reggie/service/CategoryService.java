package com.xm.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.reggie.entity.Category;

/**
 * @author YU
 */
public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
