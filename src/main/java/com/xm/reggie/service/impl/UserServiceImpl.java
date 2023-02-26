package com.xm.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.reggie.entity.User;
import com.xm.reggie.mapper.UserMapper;
import com.xm.reggie.service.UserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
