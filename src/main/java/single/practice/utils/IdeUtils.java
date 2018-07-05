package single.practice.utils;


import com.huawei.taskManager.utils.StrUtil;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Set;

public abstract class IdeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeUtils.class);


    /*public static void readStream(InputStream zFileIS, StringBuffer sb, int maxlen, String errorCode) throws IOException, ServiceException {
        byte[] b = new byte[4096];
        int count = 0;
        int fileSize = 0;
        while ((count = zFileIS.read(b)) != -1) {
            fileSize += count;
            if (fileSize > maxlen) {
                logger.info(Logger.EVENT_FAILURE,"file size too big errorcode:"+errorCode);
                throw new ServiceException(errorCode);
            }
            sb.append(new String(b, 0, count, "utf-8"));
        }
    }*/

    public static void releaseStream(Reader in){
        try{
            if(in != null ){
                in.close();
            }
        }catch (IOException e){
            LOGGER.info("close stream failed ");
        }
    }

    public static void releaseStream(FileInputStream in){
        try{
            if(in != null ){
                in.close();
            }
        }catch (IOException e){
            LOGGER.info("close stream failed ");
        }
    }

    public static void releaseStream(InputStream in){
        try{
            if(in != null ){
                in.close();
            }
        }catch (IOException e){
            LOGGER.info("close stream failed ");
        }
    }

    public static void releaseStream(FileOutputStream out){
        try{
            if(out != null ){
                out.close();
            }
        }catch (IOException e){
            LOGGER.info("close stream failed ");
        }
    }

    public static void releaseStream(OutputStream out){
        try{
            if(out != null ){
                out.close();
            }
        }catch (IOException e){
            LOGGER.info("close stream failed ");
        }
    }

    public static void releaseStream(Writer out){
        try{
            if(out != null ){
                out.close();
            }
        }catch (IOException e){
            LOGGER.info("close stream failed ");
        }
    }
    /**
     * url 带参统一格式化方法
     * 如接口如下http://ip:port?id=1&name=hello
     * 请在使用统一格式化时传入JSON params 切json如下 {"id":"1","name":"hello"}
     * 如果url不带参，传入null 或空的json都可以
     *
     * @param url
     * @param params
     * @return
     */
    public static String formatUrl(String url, Map<String,Object> params) {
        StringBuffer urlParams = new StringBuffer();
        boolean flag = true;
        if (null != params && params.size() > 0) {
            urlParams.append("?");
            Set<String> keys = params.keySet();
            try {
                for (Object object : keys) {
                    String key = String.valueOf(object);
                    if(params.get(key)==null){
                        continue;
                    }
                    String encodeParam = null;
                    encodeParam = ESAPI.encoder().encodeForURL( params.get(key).toString());
                    urlParams.append(String.format("%s=%s&", key, encodeParam));
                }
            } catch (EncodingException e ) {
                LOGGER.error(StrUtil.toOneLine("encode failed"));
            }
            if(urlParams.lastIndexOf("&")>=0) {
                urlParams.deleteCharAt(urlParams.lastIndexOf("&"));
            }else{
                flag=false;
            }
        }

        if(flag) {
            return url + urlParams.toString();
        }else{
            return url;
        }
    }

}
