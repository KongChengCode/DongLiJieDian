package com.bjpowernode.crm.settings.web.controller;

import com.alibaba.druid.pool.PreparedStatementPool;
import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(){
        return "settings/qx/user/login";
    }



    @Autowired
    private UserService userService;

    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody
    public Object login(String loginAct, String loginPwd, String isRemPwd,
                        HttpServletRequest request, HttpSession session, HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userService.queryUserByLoginActAndPwd(map);

        //System.out.println("loginAct = " + loginAct);

        ReturnObject returnObject = new ReturnObject();
        if (user == null) {
            //登陆失败
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或者密码错误");
        }else {

            String newDate = DateUtils.formatDateTime(new Date());
            /**
             * 比较两个时间的大小方法
             * 1）将数据库表中的时间varchar转化成Date，然后利用getCurrentTime()的大小来比较两个时间的大小；
             * 2）将Date类型的变量转化为固定类型的String变量，然后再来比较两个String变量
             */
            if (user.getExpireTime().compareTo(newDate) < 0) {
                //登陆失败，过期
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号已过期");
            }else if("0".equalsIgnoreCase(user.getLockState())){
                //登陆失败，状态限制
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("状态被锁定");
            }else if(!user.getAllowIps().contains(request.getRemoteAddr())){
                //登陆失败
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("IP受限");
            }else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);

                //把user保存到session中
                session.setAttribute(Constants.SESSION_USER,user);

                //如果需要记住密码，则往外写cookie
                if ("true".equals(isRemPwd)) {
                    Cookie cookie1 = new Cookie("loginAct", user.getLoginAct());
                    cookie1.setMaxAge(10*24*3600);//设置cookie存活时间
                    response.addCookie(cookie1);//响应回浏览器

                    Cookie cookie2 = new Cookie("loginPwd", user.getLoginPwd());
                    cookie2.setMaxAge(10*24*3600);
                    response.addCookie(cookie2);
                }else {
                    //把没有过期的cookie删除
                    Cookie cookie1 = new Cookie("loginAct", "1");
                    cookie1.setMaxAge(0);
                    response.addCookie(cookie1);

                    Cookie cookie2 = new Cookie("loginPwd", "1");
                    cookie2.setMaxAge(0);
                    response.addCookie(cookie2);
                }
            }
        }
        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response,HttpSession session){
        //清空cookie
        Cookie cookie1 = new Cookie("loginAct","1");
        cookie1.setMaxAge(0);
        response.addCookie(cookie1);

        Cookie cookie2 = new Cookie("loginPwd", "1");
        cookie2.setMaxAge(0);
        response.addCookie(cookie2);

        //销毁session
        session.invalidate();

        //跳转到首页
        return "redirect:/";
    }
}
