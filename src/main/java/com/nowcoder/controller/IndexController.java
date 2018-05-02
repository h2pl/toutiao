package com.nowcoder.controller;

import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by 周杰伦 on 2018/5/2.
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ToutiaoService toutiaoService;

    @RequestMapping(path = {"/", "/index"})
    @ResponseBody
    public String index(HttpSession session) {
        toutiaoService.say();
        logger.info("index");
        logger.warn("");
        return "Hello NowCoder <br> " + session.getAttribute("name") + "<br>" + toutiaoService.say();
    }

    @RequestMapping(value = {"profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "nowcoder") String key) {
        return String.format("GID{%s},UID{%d},TYPE{%d},KEY{%s}", groupId, userId, type, key);
    }
    @RequestMapping(value = {"/vm"})
    public String news(Model model) {
        model.addAttribute("key","zuochengyun");
        List<String > colors = Arrays.asList(new String[] {"red", "blue", "green"});
        model.addAttribute("colors", colors);
        Map<Integer,String> map = new HashMap<>();
        for (int i = 0;i < 4;i ++) {
            map.put(i, String.valueOf(i));
        }
        model.addAttribute("map", map);
        User user = new User();
        user.setName("jim");
        model.addAttribute("user", user);
        return "news";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session) {
        StringBuffer sb = new StringBuffer();
        Enumeration<String> headNames = request.getHeaderNames();
        List<String> names = new ArrayList<>();
        while (headNames.hasMoreElements()) {
            String name = headNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        for (Cookie cookie : request.getCookies()) {
            sb.append("cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }

        sb.append("getMethod:" + request.getMethod());
        sb.append("<br>");
        sb.append("getPathInfo:" + request.getPathInfo());
        sb.append("<br>");
        sb.append("getSession:" + request.getSession());
        sb.append("<br>");
        sb.append("getRequestURL:" + request.getRequestURL());


        return sb.toString();
    }

    @RequestMapping("response")
    @ResponseBody
    public String response(HttpServletResponse response,
                           HttpServletRequest request,
                           @CookieValue(value = "nowcoderId", defaultValue = "a") String nowcoderId,
                           @RequestParam("key") String key,
                           @RequestParam("value") String value) {
        //使用cookie注解可以直接获取cookie值而不需要引入request以及其他逻辑
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals(key)) {
//                return cookie.getName() + ":" + cookie.getValue();
//            }
//        }
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "NowcoderId from cookie:" + nowcoderId;

        //return
    }

    @RequestMapping("/redirect/{code}")
    public RedirectView redirect(@PathVariable("code") int code) {
        RedirectView red = new RedirectView("/",true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        if (code == 302) {
            red.setStatusCode(HttpStatus.MOVED_TEMPORARILY);
        }
        return red;
    }

    //该方法与上面实现的功能相同
    @RequestMapping("red")
    public String red(HttpSession session) {
        session.setAttribute("name","zuoshen");
        return "redirect:/";

    }
    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam("name") String name) {
        if (name.equals("admin")) {
            return "hello admin";
        }
        throw new IllegalArgumentException("无管理员权限");
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }
}
