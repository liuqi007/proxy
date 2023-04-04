package com.free.controller;

import com.free.common.MachineService;
import com.free.common.request.MachineReq;
import com.free.common.response.CommonResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    MachineService machineService;

    @PostMapping(value = "/update")
    public CommonResult update(@RequestBody MachineReq req) {
        return null;
    }

    @GetMapping(value = "/restart")
    public CommonResult restart() {
        machineService.startJmeter();
        return CommonResult.success();
    }

}