package com.free.common;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.free.common.utils.ExecUtil;
import com.free.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MachineService {
//    @Value("${JmeterHome}")
//    private String jmeterHome;
//
//    @Value("${callback}")
//    private String callback;
//
//    @Value("${downUrl}")
//    private String downUrl;


    private static final String JMETER = "jmeter";
    private static final String AGENT = "agent";
    private static final String STARTJMETER = "/home/liuqi/apache-jmeter-5.5/bin/jmeter_new.sh 11913 -s -j jmeter-server.log 2>&1 &";
    private static final String KILLJMETER = "ps -ef|grep jmeter | grep -v grep | awk '{print $2}' | xargs kill -9";
    private static final String JMETERDIRECT = "rm -rf /home/banssss/apache5.55/ &";
    private static final String GIVENPERMISS = "chmod +x /home/liuqi/apache-jmeter-5.5/bin/*";
    private static final String JMETERCMD = "ps -ef|grep jmeter | grep -v grep | wc -l";
    private static final String AGENTCMD = "ps aux|grep java | grep -v grep | grep agent | wc -l";

    public void killJmeter() {
        try {
            run(KILLJMETER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean startJmeter() {
        log.info("start jmeter");
        killJmeter();
        try{
            String cmd = STARTJMETER;
            ExecUtil.exec(cmd);
            Thread.sleep(3*1000);
            if(Integer.parseInt(getJmeterStatus().get(JMETER)) == 3) {
                log.info("start success");
                return true;
            }
        }catch (Exception e){
            log.error("start jmeter fail: {}", e);
            return false;
        }
        return true;
    }

    private Map<String, String> getJmeterStatus() {
        log.info("jmetrer status");
        Map<String, String> rm = new HashMap<>();
        try {
            String jc = ExecUtil.execCmd(JMETERCMD);
            String ac = ExecUtil.execCmd(AGENTCMD);
            rm.put(JMETER, jc);
            rm.put(AGENT, ac);
            log.info("JMETER:{} AGENT:{}", jc, ac);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getJmeterStatus.error:{}", e.getMessage());
        }
        return rm;
    }

    public void doRestart(String callback) throws UnknownHostException {
        killJmeter();
        if(startJmeter()) {
            sendPost(callback);
        } else {
            if(startJmeter()) {
                sendPost(callback);
            }
        }
        log.info("doRestart finished");
    }

    public void run(String command) throws Exception{
        try{
            if (!ExecUtil.exec(command)) {
                log.info("llllfail");
            } else{
                log.info("success");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendPost(String url) throws UnknownHostException {
        Map<String, String> h = new HashMap<>();
        h.put("Content-Type", "application/json");

        Map<String, String> map = new HashMap<>();
        map.put("jmeter", getJmeterStatus().get("jmeter"));
        map.put("ip", InetAddress.getLocalHost().getHostAddress());

        HttpRequest httpRequest = HttpUtil.createPost(url).addHeaders(h).body(JSONUtil.toJsonStr(map));
        HttpResponse res = httpRequest.execute();
        log.info("url:{} res:{}", url, res);

    }

    public void upgrade(String callback, String downloaUrl) throws Exception {
        killJmeter();
        downloadJmeter(downloaUrl);
        doRestart(callback);
    }

    private void downloadJmeter(String downloaUrl) throws Exception {
        //删除本地曾经下载过的jmeter.zip
        File jmeterFile = new File("/home/freespace/jmeter.zip");
        if (jmeterFile.exists()) {
            jmeterFile.delete();
        }

        //下载到新的jmeter.zip到/home/freespace/jmeter.zip
        FileUtils.downloadFile1(downloaUrl, "/home/freespace/jmeter.zip");
        File file = new File("/home/freespace/jmeter.zip");
        if (file.exists()) {
            log.info("下载完成");
        }

        //删除之前已经解压的jmeter
        run("rm -rf /home/freespace/apache-jmeter-5.5/ &");

        //解压重新下载的jmeter
        ExecUtil.unzip(file, "/home/freespace/");

        //修改文件执行权限
        run("chmod +x /home/freespace/apache-jmeter-5.5/bin/*");
    }
}
