package com.free.controller;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.free.common.request.JobReq;
import com.free.common.response.CommonResult;
import com.free.common.utils.ExecUtil;
import com.free.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {

    @Value("${JmeterHome}")
    private String jmeterHome;

    /**
     *
     * @param jobReq
     * @return
     */
    @PostMapping("/download")
    @ResponseBody
    public CommonResult download(@RequestBody Map<String, Object> jobReq) {
        log.info("jobReq:{}", jobReq);
        try{
            String fileServerUrl = (String) jobReq.get("fileServerUrl");
            JSONObject job = JSONUtil.parseObj(jobReq.get("job"));
            JSONObject scriptFile = JSONUtil.parseObj(jobReq.get("scriptFile"));
            JSONArray pluginFiles = JSONUtil.parseArray(jobReq.get("pluginFiles"));
            JSONArray paramFiles = JSONUtil.parseArray(jobReq.get("paramFiles"));

            //下载脚本文件
            String fullFilePath = fileServerUrl + scriptFile.get("path").toString().replace("/var/www/html", "");
            Integer jobId = (Integer) job.get("id");
            Integer testplanId = (Integer) job.get("testplanId");
            String jmxLocalDir = jmeterHome + File.separator + "jmx" + File.separator + testplanId + File.separator + jobId;
            String jmxLocalFile = jmxLocalDir + File.separator + fullFilePath.substring(fullFilePath.lastIndexOf("/") + 1);
            FileUtils.downloadFile1(fullFilePath, jmxLocalFile);
            log.info("download jmx fullFilePath:{} jmxLocalFile:{}", fullFilePath, jmxLocalFile);

            //下载插件文件
            String pluginStoreDir = jmeterHome + File.separator + "lib/ext";
            for (Object pluginFile : pluginFiles) {
                String pluginFilePath = fileServerUrl + ((String) pluginFile).replace("/var/www/html", "");
                String fileName = pluginFilePath.substring(pluginFilePath.lastIndexOf("/"));
                String pluginLocalFile = pluginStoreDir + File.separator + fileName;
                FileUtils.downloadFile1(pluginFilePath, pluginLocalFile);
                log.info("download plugin pluginFilePath:{} pluginLocalFile:{}", pluginFilePath, pluginLocalFile);
            }

            //下载参数文件
            for (Object paramFile : paramFiles) {
                String paramFilePath = fileServerUrl + ((String) paramFile).replace("/var/www/html", "");
                String fileName = paramFilePath.substring(paramFilePath.lastIndexOf("/"));
                String paramLocalFile = jmxLocalDir + File.separator + fileName;
                FileUtils.downloadFile1(paramFilePath, paramLocalFile);
                log.info("download plugin paramFilePath:{} paramLocalFile:{}", paramFilePath, paramLocalFile);
            }

            return CommonResult.success();
        }catch (Exception e){
            log.error(ExceptionUtil.getMessage(e));
            return CommonResult.failed(e.getMessage());
        }
    }

    @PostMapping("/deletePluginFile")
    @ResponseBody
    public CommonResult deletePluginFile(@RequestBody JobReq jobReq) {
        try{
            String pluginStoreDir = jmeterHome + File.separator + "lib/ext";
            List<String> pluginList = jobReq.getPluginList();
            for (String plugin : pluginList) {
               // delete from lib/ext
            }
            return CommonResult.success();
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/getJobLog/{jobId}")
    public CommonResult getJobLog(@PathVariable("jobId") String jobId) {
        try{
           String last = "tail -500 " +jmeterHome+ "/logs/JOB_"+jobId+".log";
           String lastMsg = ExecUtil.execCmd(last, true);

            return CommonResult.success(lastMsg);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping("/getAssertLog/{jobId}")
    public CommonResult getAssertLog(@PathVariable("jobId") String jobId) {
        try{
            String last = "tail -500 " +jmeterHome+ "/logs/ASSERT_ERR_"+jobId+".log";
            String lastMsg = ExecUtil.execCmd(last, true);

            return CommonResult.success(lastMsg);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping("/searchAssertJobLog")
    public CommonResult searchAssertJobLog(@RequestBody JobReq jobReq) {
        try{
            String fileName = jmeterHome + "/logs/ASSERT_ERR_" + jobReq.getJobId() + ".log";

            String assertMsg = "";

            if(jobReq.getSerachContent()==null){
                String tail = "tail -n '" +jobReq.getSearchLimt()+ "' " +fileName;
                assertMsg = ExecUtil.search(tail);
            }else{
                String tail = "cat "+fileName+" | grep '" +jobReq.getSerachContent()+ "' | tail -n " +jobReq.getSearchLimt();
                assertMsg = ExecUtil.search(tail);
            }


            return CommonResult.success(assertMsg);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
}
