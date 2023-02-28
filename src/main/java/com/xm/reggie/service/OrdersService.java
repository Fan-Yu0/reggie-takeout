package com.xm.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.reggie.common.R;
import com.xm.reggie.entity.Orders;

import java.util.Date;


public interface OrdersService extends IService<Orders> {


    /**
     * 提交订单
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 分页展示订单详情（管理员端展示）
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    R<Page> page(int page, int pageSize, String number, Date beginTime, Date endTime);

    /**
     * 用户分页查询
     * @param page
     * @param pageSize
     * @return
     */
    R<IPage> userPage(Integer page, Integer pageSize);

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    R<String> modifyStatus(Orders orders);



    /**
     * 根据订单id查询订单状态
     * @param orderId
     * @return
     */
    R<String> findStatusById(Long orderId);
}
