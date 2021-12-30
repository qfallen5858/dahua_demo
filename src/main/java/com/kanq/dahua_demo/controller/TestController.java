package com.kanq.dahua_demo.controller;

import com.netsdk.util.DeviceInit;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping(value = "/say")
    public String test(){
        DeviceInit.search("192.168.1.108");
        return "success";
    }
}
