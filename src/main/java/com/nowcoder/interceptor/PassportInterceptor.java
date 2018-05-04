package com.nowcoder.interceptor;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by 周杰伦 on 2018/5/4.
 */
@Component
public class PassportInterceptor implements HandlerInterceptor{
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private HostHolder hostHolder;

    //true继续请求，false拒绝请求
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //处理用户信息，判断是否有ticket,一个用户一个ticket，但是有时限
        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
            //判断ticket是否过期和无效
            if (ticket != null) {
                LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
                if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                    return true;
                }
                else {
                    User user = userDAO.selectById(loginTicket.getUserId());
                    hostHolder.setUsers(user);
                    return true;
                    //不能直接放在request里，因为是全局的一个ticket，其他服务想要读取时可能不会用到httprequest请求，
                    // 但是可以注入hostholder来获取用户信息。
                }
            }
        }
        return true;
    }

    //渲染之前提供的后处理方法，可以添加模型数据，自动传给前端。
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject(hostHolder.getUser());
            hostHolder.clear();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
