package com.nowcoder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 周杰伦 on 2018/5/2.
 */
@Controller
public class SettingController {
    @RequestMapping("setting")
    @ResponseBody
    public String setting() {
        return "hello";
    }
}
