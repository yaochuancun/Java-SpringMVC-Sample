//package single.practice.utils;
//
//import org.owasp.esapi.ESAPI;
//import org.owasp.esapi.Validator;
//import org.owasp.esapi.errors.EncodingException;
//import org.owasp.esapi.errors.ValidationException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URL;
//import java.util.Map;
//import java.util.Set;
//
//public abstract class UrlUtil {
//
//    private static final Logger logger = LoggerFactory.getLogger(UrlUtil.class);
//    private static final Validator validator  = ESAPI.validator();
//
//    public static URI toUri(String uri) {
//        try{
//            String validatorUri = validator.getValidInput("URI_VALIDATION",uri,"URL", 300, false);
//            return URI.create(validatorUri).normalize();
//        }catch (ValidationException e){
//            logger.error("getRequestUrl fail");
//        }
//        return null;
//    }
//
//    public static URL toURL(String uri) throws MalformedURLException {
//        return URI.create(uri).normalize().toURL();
//    }
//
//    /**
//     * url 带参统一格式化方法
//     * 如接口如下http://ip:port?id=1&name=hello
//     * 请在使用统一格式化时传入JSON params 切json如下 {"id":"1","name":"hello"}
//     * 如果url不带参，传入null 或空的json都可以
//     *
//     * @param url
//     * @param params
//     * @return
//     */
//    public static String formatUrl(String url, Map<String,Object> params) {
//        StringBuffer urlParams = new StringBuffer();
//        boolean flag = true;
//        if (null != params && params.size() > 0) {
//            urlParams.append("?");
//            Set<String> keys = params.keySet();
//            try {
//                for (Object object : keys) {
//                    String key = String.valueOf(object);
//                    if(params.get(key)==null){
//                        continue;
//                    }
//                    String encodeParam = null;
//                    encodeParam = ESAPI.encoder().encodeForURL( params.get(key).toString());
//                    urlParams.append(String.format("%s=%s&", key, encodeParam));
//                }
//            } catch (EncodingException e ) {
//                logger.error(StrUtils.toOneLine("encode failed"));
//            }
//            if(urlParams.lastIndexOf("&")>=0) {
//                urlParams.deleteCharAt(urlParams.lastIndexOf("&"));
//            }else{
//                flag=false;
//            }
//        }
//
//        if(flag) {
//            return url + urlParams.toString();
//        }else{
//            return url;
//        }
//    }
//}
