package com.free.controller;


import com.free.common.request.JobReq;
import com.free.common.response.CommonResult;
import com.free.common.utils.ExecUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
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

    @Value("${JmxHome}")
    private String jmxHome;

    /**
     *
     * @param jobReq
     * @return
     */
    @PostMapping("/downJmxFile")
    @ResponseBody
    public CommonResult downJmxFile(@RequestBody Map<String, String> jobReq) {
      try{
          String fileName = jobReq.get("fileName");
          String fullJmxFilePath = jobReq.get("filePath")  +File.separator +fileName;
          File file = new File(fullJmxFilePath);

          if(file.exists() && file.isFile()){
              file.delete();
          }

          //download code .......
          //downlad(fileName, fullJmxFilePath)
          return CommonResult.success();
      }catch (Exception e){
          return CommonResult.failed(e.getMessage());
      }
    }

    @PostMapping("/downParamFile")
    @ResponseBody
    public CommonResult downParamFile(@RequestBody Map<String, String> jobReq) {
        try{
            final String separatorChar = "/";
            jobReq.keySet().parallelStream().forEach(fileName->{
                //downlad(fileName, fullJmxFilePath)
            });

            return CommonResult.success();
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @PostMapping("/downPluginFile")
    @ResponseBody
    public CommonResult downPluginFile(@RequestBody JobReq jobReq) {
        try{
            String pluginStoreDir = jmeterHome + File.separator + "lib/ext";
            List<String> pluginList = jobReq.getPluginList();
            for (String plugin : pluginList) {
                String fileName = plugin;
                if (fileName.contains("/")) {
                    fileName = fileName.substring(fileName.lastIndexOf("/"));
                }

                File file = null;// download code
                if(!file.exists()){
                    return CommonResult.failed("下载文件失败");
                }
            }
            return CommonResult.success();
        }catch (Exception e){
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
