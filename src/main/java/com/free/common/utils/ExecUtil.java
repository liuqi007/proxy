package com.free.common.utils;

import ch.qos.logback.core.util.TimeUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class ExecUtil {
    private static ThreadPoolExecutor executor;

    static {
        ThreadFactory namedThreadFactory = new DefaultThreadFactory("jmeter agent");
        executor = new ThreadPoolExecutor(6,10,5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public static boolean exec(String cmd) throws IOException, InterruptedException{
        String[] cmds = {"/bin/bash", "-c", cmd};
        Process process = Runtime.getRuntime().exec(cmds);

        clearStream(process.getInputStream());

        clearStream(process.getErrorStream());

        int i = process.waitFor();
        if(i!=0){
            return false;
        }
        return true;
    }

    private static void clearStream(InputStream stream){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String line = null;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(stream));){
                    while ((line=in.readLine())!=null){
                        log.info(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String execCmd(String cmd) throws Exception{
        return execCmd(cmd, false);
    }

    public static String execCmd(String cmd, boolean isNewLine) throws Exception{
        String[] cmds = {"/bin/bash", "-c", cmd};
        Process process = Runtime.getRuntime().exec(cmds);
        StringBuffer stringBuffer = new StringBuffer();
        try(InputStream inputStream = process.getInputStream()){
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line;
            while((line=reader.readLine())!=null){
                if(isNewLine){
                    stringBuffer.append(line).append("\n");
                }else {
                    stringBuffer.append(line);
                }
            }
        }catch (Exception e){

        }
        if(process.waitFor()==0){
            log.info("执行成功");
        }else {
            log.info("执行失败");
        }
        return stringBuffer.toString();
    }

    public static String search(String cmd) throws Exception{
        String[] cmds = {"/bin/bash", "-c", cmd};
        Process process = Runtime.getRuntime().exec(cmds);
        StringBuffer stringBuffer = new StringBuffer();
        try(InputStream inputStream = process.getInputStream()){
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line;
            while((line=reader.readLine())!=null){

                    stringBuffer.append(line).append("\n");

            }
        }catch (Exception e){

        }
        if(process.waitFor()==0){
            log.info("执行成功");
        }else {
            log.info("执行失败");
        }
        return stringBuffer.toString();
    }

    static  int BUFFER_SIZE=1024;

    public static void unzip(File srcFile, String destDirPath) {
        if (!srcFile.exists()) {
//            throw new exec
        }

        if("".equals(destDirPath)){
            destDirPath = System.getProperty("user.dir");
        }

        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(srcFile, Charset.forName("gbk"));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();

                }else{
                    File targetFile = new File(destDirPath + "/" + entry.getName());

                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }

                    targetFile.createNewFile();

                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while((len=is.read(buf))!=-1){
                        fos.write(buf, 0, len);
                    }

                    fos.close();
                    is.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(zipFile!=null){
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
