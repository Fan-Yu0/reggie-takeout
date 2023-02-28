package com.xm.reggie.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.reggie.common.R;
import com.xm.reggie.entity.Orders;
import com.xm.reggie.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;


    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        ordersService.submit(orders);
        return R.success("11");
    }

    /**
     * 分页展示订单详情（管理员端展示）
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, Date beginTime, Date endTime){
        return ordersService.page(page,pageSize,number,beginTime,endTime);
    }

    /**
     * 订单详情
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<IPage> userPage(Integer page, Integer pageSize){
        return ordersService.userPage(page,pageSize);
    }

    /**
     * 修改订单
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        return ordersService.modifyStatus(orders);
    }

    /**
     * 根据id查找订单
     * @param orderId
     * @return
     */
    @GetMapping("/findStatusById")
    public R<String> findById(Long orderId){
        return ordersService.findStatusById(orderId);
    }
}
