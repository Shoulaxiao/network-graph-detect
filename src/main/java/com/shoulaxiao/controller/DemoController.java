package com.shoulaxiao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 15:58
 **/
@Controller
@RequestMapping(value = "/api/test")
public class DemoController {


    @RequestMapping("/hello")
    public String test(){
        return "login";
    }

}
