package com.free.common.utils;


import cn.hutool.core.io.FileUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileUtils {
    /**
     * 下载远程文件并保存到本地
     *
     * @param remoteFilePath-远程文件路径
     * @param localFilePath-本地文件路径（带文件名）
     */
    public static void downloadFile1(String remoteFilePath, String localFilePath) {
        URL urlfile = null;
        URLConnection urlConnection = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File f = new File(localFilePath);
        try {
            urlfile = new URL(remoteFilePath);
            urlConnection = urlfile.openConnection();
            urlConnection.connect();
            FileUtil.writeFromStream(urlConnection.getInputStream(), localFilePath);
//            bis = new BufferedInputStream(httpUrl.getInputStream());
//            bos = new BufferedOutputStream(new FileOutputStream(f));
//            int len = 2048;
//            byte[] b = new byte[len];
//            while ((len = bis.read(b)) != -1) {
//                bos.write(b, 0, len);
//            }
//            bos.flush();
//            bis.close();
//            httpUrl.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载远程文件并保存到本地
     *
     * @param remoteFilePath-远程文件路径
     * @param localFilePath-本地文件路径（带文件名）
     */
    public static void downloadFile2(String remoteFilePath, String localFilePath) {
        URL website = null;
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        try {
            website = new URL(remoteFilePath);
            rbc = Channels.newChannel(website.openStream());
            fos = new FileOutputStream(localFilePath);//本地要存储的文件地址 例如：test.txt
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (rbc != null) {
                try {
                    rbc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 小试牛刀
     *
     * @param args
     */
    public static void main(String[] args) {
        /*远程文件路径*/
        String remoteFilePath1 = "https://tenfei01.cfp.cn/creative/vcg/800/new/VCG211157640278-VXD.jpg";
        String remoteFilePath2 = "https://pic.3gbizhi.com/2019/1112/20191112013312648.jpg";
        /*本地文件路径（带文件名）*/
        String localFilePath1 = "E:\\LeStoreDownload\\update\\广州塔.jpg";
        String localFilePath2 = "E:\\LeStoreDownload\\update\\大桥.jpg";
        downloadFile1(remoteFilePath1, localFilePath1);
        downloadFile2(remoteFilePath2, localFilePath2);
    }
}


//public void httpDownload(String httpUrl,HttpServletResponse response)throws Exception{
//        URL url=new URL(httpUrl);
//        HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
//        urlConnection.connect();
//        OutputStream outputStream=response.getOutputStream();
//        InputStream inputStream=urlConnection.getInputStream();
//        IOUtils.copy(inputStream,outputStream);
//        inputStream.close();
//        response.flushBuffer();
//        outputStream.close();
//        }
//        }
