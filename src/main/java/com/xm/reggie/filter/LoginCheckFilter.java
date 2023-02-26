package com.xm.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.xm.reggie.common.BaseContext;
import com.xm.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author YU
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter{

    //路径匹配器
    public static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取请求路径
        String requestURI = request.getRequestURI();

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls,requestURI);

        //3.如果不需要处理，直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }

        //4.1 如果需要处理，判断用户是否登录
        if(request.getSession().getAttribute("employee") !=null){

            //如果登录了，将用户id存入ThreadLocal
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentUser(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //4.2 如果需要处理，判断用户是否登录
        if(request.getSession().getAttribute("user") !=null){

            //如果登录了，将用户id存入ThreadLocal
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentUser(userId);

            filterChain.doFilter(request,response);
            return;
        }

        //5.如果未登录则返回未登录结果，通过输出流向客户端返回数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            if(PATH_MATCHER.match(url,requestURI)){
                return true;
            }
        }
        return false;
    }

}
