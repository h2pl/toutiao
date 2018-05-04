package com.nowcoder.controller;

import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by 周杰伦 on 2018/5/4.
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String register(@RequestParam("username") String userName,
                           @RequestParam("password") String password) {
        try {
            Map<String, Object> map = userService.register(userName, password);
            if (map.isEmpty()) {
                map.put("msg","注册成功");
                return ToutiaoUtil.getJSONString(0, map);
            }else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        }catch (Exception e) {
            logger.error("注册异常" +  e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常" +  e.getMessage());
        }
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam("username") String userName,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int remember,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(userName, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");//设置cookie全站有效
                //cookie默认关闭浏览器后就删除，所以要设置时间
                if (remember > 0) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                //响应中传入cookie
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "登陆成功");
            }else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        }catch (Exception e) {
            logger.error("登陆异常" +  e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登陆异常" +  e.getMessage());
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}
