package com.xm.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xm.reggie.common.R;
import com.xm.reggie.entity.User;
import com.xm.reggie.service.UserService;
import com.xm.reggie.utils.SMSUtils;
import com.xm.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 发送手机验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //调用阿里云短信
            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);

            //生成验证码保存到Session
//            session.setAttribute(phone,code);

            //生成验证码保存到Redis
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("短信发送成功");
        }

        return R.error("短信发送失败");
    }


    /**
     * 移动端登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        String phone = (String) map.get("phone");

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取验证码
//        Object code1 = session.getAttribute(phone);

        //从Redis中获取验证码
        Object code1 = redisTemplate.opsForValue().get(phone);

        //比较验证码是否正确(页面输入的验证码和Session中的验证码是否一致)
        if(code.equals(code1)){
            //判断是否是新用户(根据手机号查询用户信息)，如果是新用户，自动注册
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);
            if(user == null){
                //自动注册
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //删除验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("验证码错误");
    }


}
