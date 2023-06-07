package com.free.controller;

import com.free.common.MachineService;
import com.free.common.request.MachineReq;
import com.free.common.response.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    MachineService machineService;

    @PostMapping("/upgrade")
    @ResponseBody
    public CommonResult upgrade(@RequestBody MachineReq req) throws Exception {
        machineService.upgrade(req.getCallback(), req.getDownloaUrl());
        return CommonResult.success();
    }

    @PostMapping("/restart")
    public CommonResult restart(@RequestBody MachineReq req) throws UnknownHostException {
        machineService.doRestart(req.getCallback());
        return CommonResult.success();
    }

}