package com.xm.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xm.reggie.common.BaseContext;
import com.xm.reggie.common.R;
import com.xm.reggie.entity.ShoppingCart;
import com.xm.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        LambdaUpdateWrapper<ShoppingCart> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);

        //查询是菜品或套餐否在购物车中
        if(shoppingCart.getDishId() != null){
            //菜品
            wrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else {
            //套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //sql: select * from shopping_cart where user_id = ? and dish_id = ? or setmeal_id = ?
        ShoppingCart cart = shoppingCartService.getOne(wrapper);
        //如果存在，数量加一
        if(cart != null){
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartService.updateById(cart);
        }else {
            //如果不存在，添加到购物车
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }
        return R.success(cart);
    }

    /**
     * 删除购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        LambdaUpdateWrapper<ShoppingCart> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);

        //查询是菜品或套餐否在购物车中
        if(shoppingCart.getDishId() != null){
            //菜品
            wrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else {
            //套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //sql: select * from shopping_cart where user_id = ? and dish_id = ? or setmeal_id = ?
        ShoppingCart cart = shoppingCartService.getOne(wrapper);
        //如果存在，数量减一
        if(cart.getNumber()-1 >= 1 ){
            cart.setNumber(cart.getNumber() - 1);
            shoppingCartService.updateById(cart);
        }else{
            shoppingCartService.remove(wrapper);
        }
        return R.success(cart);
    }


    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<ShoppingCart> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId).orderByAsc(ShoppingCart::getCreateTime);

        //sql: select * from shopping_cart where user_id = ? order by create_time asc
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        Long userId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<ShoppingCart> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(wrapper);

        return R.success("清空购物车成功");

    }

}
