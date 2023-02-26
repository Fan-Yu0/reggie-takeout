package com.xm.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.reggie.entity.ShoppingCart;
import com.xm.reggie.mapper.ShoppingCartMapper;
import com.xm.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;


@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
