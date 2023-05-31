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

import java.net.UnknownHostException;

@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    MachineService machineService;

    @PostMapping(value = "/upgrade")
    public CommonResult upgrade(@RequestBody MachineReq req) throws Exception {
        machineService.upgrade(req.getCallback(), req.getDownloaUrl());
        return CommonResult.success();
    }

    @GetMapping(value = "/restart")
    public CommonResult restart(@RequestBody MachineReq req) throws UnknownHostException {
        machineService.doRestart(req.getCallback());
        return CommonResult.success();
    }

}