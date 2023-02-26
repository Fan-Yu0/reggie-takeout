package com.xm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.reggie.common.R;
import com.xm.reggie.entity.Employee;
import com.xm.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author YU
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据用户名username查询数据库
        LambdaUpdateWrapper<Employee> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到数据，返回错误信息
        if(emp == null){
            return R.error("没有找到用户");
        }

        //4.密码对比，如果不一致，返回错误信息
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //5.查看员工是否被禁用，如果被禁用，返回错误信息
        if(emp.getStatus() == 0){
            return R.error("当前账号被禁用");
        }

        //6.登录成功，将员工id存入session中并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    /**
     * 员工退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1.清除session中保存的当前的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee) {

        //设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.save(employee);
        return R.success("新增成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造查询条件构造器
        LambdaUpdateWrapper<Employee> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);

        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return R.error("没有找到该员工");
        }
        return R.success(employee);
    }
}
