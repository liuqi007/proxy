package com.free.common;

import com.free.common.utils.ExecUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


    private static final String KILLJMETER = "ps -ef|grep jmeter | grep -v grep | awk '{print $2}' | xargs kill -9";
    private static final String JMETERDIRECT = "rm -rf /home/banssss/apache5.55/ &";
    private static final String GIVENPERMISS = "chmod +x /home/liuqi/apache-jmeter-5.5/bin/*";
//    private static final String KILLJMETER = "ps -ef|grep jmeter | grep -v grep | awk '{print $2}' | xargs kill -9";

    public void killJmeter() {
        try {
            run(KILLJMETER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public HashMap<String, String> getJmeterStatus(){
//        Map<String, String> resultMap = new HashMap<>();
//
//    }
//
    public boolean startJmeter() {
        log.info("start jmeter");
        try{
            String cmd = "/home/liuqi/apache-jmeter-5.5/bin/jmeter_new.sh 11913 -s -j jmeter-server.log 2>&1 &";
            ExecUtil.exec(cmd);
            Thread.sleep(3*1000);
//            if(Integer.parseInt(getJmeterStatus().get("jmeter")) == 3) {
//                log.info("start success");
//                return true;
//            }
        }catch (Exception e){
            log.error("start jmeter fail: {}", e);
            return false;
        }
        return true;
    }

    private Map<String, String> getJmeterStatus() {
        return null;
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
}
