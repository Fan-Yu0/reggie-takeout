package com.xm.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.reggie.common.BaseContext;
import com.xm.reggie.common.R;
import com.xm.reggie.dto.OrdersDto;
import com.xm.reggie.entity.*;
import com.xm.reggie.mapper.OrderDetailMapper;
import com.xm.reggie.mapper.OrdersMapper;
import com.xm.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Orders orders) {

        // 获取当前用户id
        Long uid = BaseContext.getCurrentId();

        //查询当前用户的购物车
        LambdaUpdateWrapper<ShoppingCart> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, uid);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new RuntimeException("购物车为空");
        }

        //查询用户
        User user = userService.getById(uid);

        //查询地址
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook == null){
            throw new RuntimeException("地址不存在");
        }

        //向订单表中插入数据
        long orderId = IdWorker.getId();

        //原子操作
        AtomicInteger money = new AtomicInteger(0);

        List<OrderDetail> orderDetailList = shoppingCarts.stream().map(item->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            money.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());



        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        //总金额
        orders.setAmount(new BigDecimal(money.get()));
        orders.setUserId(uid);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车数据
        shoppingCartService.remove(wrapper);
    }


    @Override
    public R<Page> page(int page,int pageSize, String number, Date beginTime, Date endTime) {
        LambdaQueryWrapper<Orders> wapper = new LambdaQueryWrapper<>();

        //sql: select * from orders where number = ? and order_time <= ? and order_time >= ?
        wapper.like(null != number,Orders::getNumber,number)
                .le(null != endTime,Orders::getOrderTime,endTime)
                .ge(null != beginTime,Orders::getOrderTime,beginTime);
        IPage<Orders> p = new Page<>(page,pageSize);

        IPage<Orders> ordersIPage = ordersMapper.selectPage(p, wapper);

        //将orders转换为ordersDto
        List<Orders> records = ordersIPage.getRecords();
        String s = JSON.toJSONString(records);
        List<OrdersDto> ordersDtos = JSON.parseArray(s, OrdersDto.class);

        //查询当前用户订单明细
        for (OrdersDto ordersDto : ordersDtos) {
            LambdaQueryWrapper<OrderDetail> wapper1 = new LambdaQueryWrapper<>();
            //sql : select * from order_detail where order_id = ?
            wapper1.eq(OrderDetail::getOrderId,ordersDto.getId());
            List<OrderDetail> orderDetails = orderDetailMapper.selectList(wapper1);
            ordersDto.setOrderDetails(orderDetails);
        }

        String s1 = JSON.toJSONString(ordersIPage);

        Page page1 = JSON.parseObject(s1, Page.class);
        page1.setRecords(ordersDtos);

        return R.success(page1);
    }

    @Override
    public R<IPage> userPage(Integer page, Integer pageSize) {
        //获取userid
        Long currentId = BaseContext.getCurrentId();
        //查询条件 sql: select * from orders where user_id = ? order by order_time desc
        LambdaQueryWrapper<Orders> wapper = new LambdaQueryWrapper<>();
        wapper.eq(Orders::getUserId,currentId);
        wapper.orderByDesc(Orders::getOrderTime);
        IPage<Orders> p = new Page<>(page,pageSize);

        //查询语句
        IPage<Orders> ordersIPage = ordersMapper.selectPage(p, wapper);
        //最终返回的数据
        IPage<OrdersDto> ordersDtoIPage = new Page<>();
        //复制数据
        BeanUtils.copyProperties(ordersIPage,ordersDtoIPage,"records");
        //遍历获取到的订单
        List<Orders> records = ordersIPage.getRecords();
        List<OrdersDto> ordersDtos = new ArrayList<>();
        for (Orders record : records) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(record,ordersDto);
            //查询订单详情
            LambdaQueryWrapper<OrderDetail> wapper1 = new LambdaQueryWrapper<>();
            wapper1.eq(OrderDetail::getOrderId,record.getId());
            List<OrderDetail> orderDetails = orderDetailMapper.selectList(wapper1);
            ordersDto.setOrderDetails(orderDetails);
            ordersDtos.add(ordersDto);
        }
        ordersDtoIPage.setRecords(ordersDtos);
        return R.success(ordersDtoIPage);
    }

    @Override
    public R<String> modifyStatus(Orders orders) {
        ordersMapper.updateById(orders);
        return R.success("修改成功");
    }

    @Override
    public R<String> findStatusById(Long orderId) {
        Orders orders = ordersMapper.selectById(orderId);
        if (orders.getStatus() == 2){
            return R.success("订单已支付");
        }
        return R.error("订单未支付");
    }
}
