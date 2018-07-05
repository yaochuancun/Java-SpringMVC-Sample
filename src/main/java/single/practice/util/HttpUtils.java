package single.practice.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import single.practice.utils.*;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.*;

public abstract class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
    private static final int DEFAULT_TIME_OUT = 20000;
    private static final int BUFFER = 1024;

    private static final String lineSeparator = "\r\n";

    /**
     * GET请求
     *
     * @param url url
     * @return HttpRequestResult HttpRequestResult
     * @throws Exception Exception
     */
    public static HttpRequestResult doGet(String url)
            throws Exception {
        return doGet(url, null, null, DEFAULT_TIME_OUT);
    }
    /**
     * @param url     url
     * @param params  params
     * @param headers headers
     * @return HttpRequestResult HttpRequestResult
     * @throws Exception Exception
     */
    public static HttpRequestResult doGet(String url, Map<String, Object> params, Map<String, String> headers)
            throws Exception {
        return doGet(url, params, headers, DEFAULT_TIME_OUT);
    }

    /**
     * @param url     url
     * @param params  params
     * @param headers headers
     * @param timeout timeout
     * @return HttpRequestResult  HttpRequestResult
     * @throws Exception Exception
     */
    private static HttpRequestResult doGet(String url, Map<String, Object> params, Map<String, String> headers,
                                           int timeout)
            throws Exception {
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = getHttpClients()) {
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                params.forEach((param,obj)->{
                    String value = null;
                    if(null != obj){
                        value = obj.toString();
                    }
                    paramList.add(new BasicNameValuePair(param, value));
                });
                String queryString = URLEncodedUtils.format(paramList, Consts.UTF_8);
                if(!StrUtil.isEmpty(queryString)){
                    url = url + "?" + queryString;
                }

            }
            httpGet = new HttpGet(UrlUtil.toUri(url));
            setTimeout(timeout, httpGet);
            addHeaders(httpGet, headers);
            response = httpclient.execute(httpGet);
            return handleResponse(response);
        }
        catch (RuntimeException re){
            LOGGER.error("runtime exception");
            throw new Exception("HttpUtil do post error");
        }
        catch (Exception e) {
            throw new Exception("HttpUtil do post error");
        }
        finally {
            HttpUtils.close(response);
            HttpUtils.releaseConnection(httpGet);
        }
    }


    /**
     * POST请求
     *
     * @param url url
     * @return HttpRequestResult HttpRequestResult
     * @throws Exception Exception
     */
//    public static HttpRequestResult doPost(String url, String apiStr)
//            throws Exception {
//        return doPost(url, null, null, DEFAULT_TIME_OUT,apiStr);
//    }

    /**
     * POST请求
     *
     * @param url url
     * @return HttpRequestResult HttpRequestResult
     * @throws Exception Exception
     */
    public static HttpRequestResult doPost(String url, String apiStr,Map<String,String> map)
            throws Exception {

        return doPost(url, null, map, DEFAULT_TIME_OUT,apiStr);
    }


    /**
     * @param url     url
     * @param postMap postMap
     * @param headers headers
     * @param timeout timeout
     * @return HttpRequestResult HttpRequestResult
     * @throws Exception Exception
     */
    private static HttpRequestResult doPost(String url, Map<String, Object> postMap, Map<String, String> headers,
                                            int timeout, String apiStr)
            throws Exception{
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = getHttpClients()) {
            httpPost = new HttpPost(UrlUtil.toUri(url));
            setTimeout(timeout, httpPost);
            if (postMap != null) {
                List<NameValuePair> nvps = new ArrayList<>();
                postMap.forEach((param,obj)->{
                    String value = null;
                    if(null != obj){
                        value = obj.toString();
                    }
                    nvps.add(new BasicNameValuePair(param, value));
                });
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            }
            String requestParam = JSONHelper.toJsonStr(apiStr);
            StringEntity stringEntity = new StringEntity(Normalizer.normalize(requestParam, Normalizer.Form.NFC),
                    "UTF-8");
            httpPost.setEntity(stringEntity);
            addHeaders(httpPost, headers);
            response = httpclient.execute(httpPost);
            return handleResponse(response);
        }catch (RuntimeException re){
            LOGGER.error("runtime exception");
            throw new Exception("HttpUtil do post error");
        }
        catch (Exception e) {
            throw new Exception("HttpUtil do post error");
        }
        finally {
            HttpUtils.close(response);
            HttpUtils.releaseConnection(httpPost);
        }
    }





    /**
     * @param url     url
     * @param postMap postMap
     * @param headers headers
     * @param timeout timeout
     * @return HttpRequestResult HttpRequestResultp
     * @throws Exception Exception
     */
    public static void downFile(String url, Map<String, Object> postMap, Map<String, String> headers,
                         int timeout, String taskID,String fileName)
            throws Exception{
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        OutputStream fo = null;
        InputStream is = null;
        int httpcode = 0;
        try (CloseableHttpClient httpclient = getHttpClients()) {
            httpPost = new HttpPost(UrlUtil.toUri(url));
            setTimeout(timeout, httpPost);
            if (postMap != null) {
                List<NameValuePair> nvps = new ArrayList<>();
                postMap.forEach((param,obj)->{
                    String value = null;
                    if(null != obj){
                        value = obj.toString();
                    }
                    nvps.add(new BasicNameValuePair(param, value));
                });
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            }
            addHeaders(httpPost, headers);
            response = httpclient.execute(httpPost);
            httpcode = response.getStatusLine().getStatusCode();
            if(httpcode>300){
                LOGGER.error("Docman error, httpcode: %d", httpcode);
            }
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            makeFolder("/devcloud/workspace/"+taskID);;
            fo=new FileOutputStream("/devcloud/workspace/"+taskID+"/"+fileName);//write to a filefolder
            byte[] by = new byte[1024 * 1024 * 10];
            int i = -1;
            while ((i = is.read(by)) != -1) {
                fo.write(by, 0, i);
            }
            fo.flush();

        }catch (RuntimeException re){
            LOGGER.error("runtime exception");
        }
        catch (Exception e) {
            throw new Exception("HttpUtil do post error");
        }
        finally {
            HttpUtils.close(response);
            HttpUtils.releaseConnection(httpPost);

                IdeUtils.releaseStream(fo);
                IdeUtils.releaseStream(is);
        }
    }

    public static HttpRequestResult doPost(String url, Map<String, Object> postMap, Map<String, String> headers,
                                            int timeout,Object objectMap)
            throws Exception{
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = getHttpClients()) {
            httpPost = new HttpPost(UrlUtil.toUri(url));
            setTimeout(timeout, httpPost);
            if (postMap != null) {
                List<NameValuePair> nvps = new ArrayList<>();
                postMap.forEach((param,obj)->{
                    String value = null;
                    if(null != obj){
                        value = obj.toString();
                    }
                    nvps.add(new BasicNameValuePair(param, value));
                });
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            }
            String requestParam = JSONHelper.toJsonStr(objectMap);
            StringEntity stringEntity = new StringEntity(Normalizer.normalize(requestParam, Normalizer.Form.NFC),
                    "UTF-8");
            httpPost.setEntity(stringEntity);
            addHeaders(httpPost, headers);
            response = httpclient.execute(httpPost);
            return handleResponse(response);
        }catch (RuntimeException re){
            LOGGER.error("runtime exception");
            throw new Exception("HttpUtil do post error");
        }
        catch (Exception e) {
            throw new Exception("HttpUtil do post error");
        }
        finally {
            HttpUtils.close(response);
            HttpUtils.releaseConnection(httpPost);
        }
    }

    public static Map<String,Object> doPostForHeaders(String url, Map<String, Object> postMap, Map<String, String> headers,
                                           int timeout,Object objectMap)
            throws Exception{
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = getHttpClients()) {
            httpPost = new HttpPost(UrlUtil.toUri(url));
            setTimeout(timeout, httpPost);
            if (postMap != null) {
                List<NameValuePair> nvps = new ArrayList<>();
                postMap.forEach((param,obj)->{
                    String value = null;
                    if(null != obj){
                        value = obj.toString();
                    }
                    nvps.add(new BasicNameValuePair(param, value));
                });
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            }
            String requestParam = JSONHelper.toJsonStr(objectMap);
            StringEntity stringEntity = new StringEntity(Normalizer.normalize(requestParam, Normalizer.Form.NFC),
                    "UTF-8");
            httpPost.setEntity(stringEntity);
            addHeaders(httpPost, headers);
            response = httpclient.execute(httpPost);
            return handleResponseHeaders(response);
        }catch (RuntimeException re){
            LOGGER.error("runtime exception");
            throw new Exception("HttpUtil do post error");
        }
        catch (Exception e) {
            throw new Exception("HttpUtil do post error");
        }
        finally {
            HttpUtils.close(response);
            HttpUtils.releaseConnection(httpPost);
        }
    }



    private static void setTimeout(int timeout, HttpRequestBase httpRequest) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        httpRequest.setConfig(requestConfig);
    }

    public static void addHeaders(HttpRequestBase httpRequest, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach((param,value)->{
                if (value != null) {
                    httpRequest.addHeader(param, value);
                }
            });
        }
    }

    /**
     * Release connection
     *
     * @param httpRequest httpRequest
     */
    public static void releaseConnection(HttpRequestBase httpRequest){

        if (httpRequest != null) {

            httpRequest.releaseConnection();

        }
    }

    //@SuppressWarnings("unused")
    public static void close(Closeable closeable) {

        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException e) {
                LOGGER.error("Close closeable error");
            }
        }
    }

    public static HttpRequestResult handleResponse(CloseableHttpResponse response)
            throws Exception {

        try{
            HttpRequestResult httpRequestResult = new HttpRequestResult();
            String result = null;
            int status = 404;//初始赋值，不具有实际意义
            StatusLine statusLine=response.getStatusLine();
            if(null!=statusLine){
                status=statusLine.getStatusCode();
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream is = entity.getContent();
                result = inputStream2String(is);
                if (entity.getContentType() != null) {
                    httpRequestResult.setResponseContentType(entity.getContentType().getValue());
                }
            }
            EntityUtils.consume(entity);
            httpRequestResult.setResponseCode(status);
            httpRequestResult.setResponseResult(result);
            return httpRequestResult;
        }catch (Exception e){
            throw new Exception("handle response abnormal");
        }

    }

    public static Map<String,Object> handleResponseHeaders(CloseableHttpResponse response)
            throws Exception {

        try{
            Map<String,Object> result = new HashMap<>();
            for(Header header:response.getAllHeaders()){
                result.put(header.getName(),header.getValue());
            }
            return result;
        }catch (Exception e){
            throw new Exception("handle response abnormal");
        }

    }


    private static String inputStream2String(InputStream is)
            throws Exception {

        if (is == null) {
            return null;
        }
        LineIterator lineIterator = null;
        try (InputStreamReader inputStreamReader = new InputStreamReader(is, Consts.UTF_8);
             BufferedReader in = new BufferedReader(inputStreamReader, BUFFER)) {
            lineIterator = new LineIterator(in);
            StringBuilder sb = new StringBuilder();
            String inputLine;
            while (lineIterator.hasNext()) {
                inputLine = lineIterator.next();
                sb.append(inputLine).append(lineSeparator);
            }
            return sb.toString();
        }catch (RuntimeException re){
            LOGGER.error("runtime exception");
            throw new Exception("HttpUtil do post error");
        }
        catch (Exception e) {
            LOGGER.error("handle http result error");
            throw new Exception("handle http result error");
        }
        finally {
            HttpUtils.close(is);
            if (lineIterator != null) {
                lineIterator.close();
            }
        }
    }

    public static CloseableHttpClient getHttpClients()
            throws Exception {

        SSLConnectionSocketFactory sslsf = getNoHostnameVerifierSSLFactory();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    private static SSLConnectionSocketFactory getNoHostnameVerifierSSLFactory()
            throws Exception {

        try{
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null,
                    (chain, authType) -> true).build();
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        }catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e){
            throw new Exception("Error in getNoHostnameVerifierSSLFactory method.");
        }

    }

    public static boolean isNormalStatusCode(String code){
        return isNormalStatusCode(Integer.parseInt(code));
    }
    public static boolean isNormalStatusCode(int code){
        if(code>=200&&code<300){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 获取文件安全输出流，增加输出文件权限控制
     *
     * @param filePath 输出流的文件路径
     * @param isGroupReadShare 是否可以被属主用户组其他用户读取。true: 输出文件权限640; false:
     *            输出文件权限600
     * @return
     * @throws IOException
     */
    public static OutputStream getSafeOutputStream(String filePath, boolean isGroupReadShare)
            throws IOException {
        File file = FileUtils.getFile(filePath);
        Path path = file.toPath();
        FileAttribute<Set<PosixFilePermission>> attr = getDefaultFileAttribute(file,isGroupReadShare);
        EnumSet<StandardOpenOption> localEnumSet = EnumSet.of(StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
        java.nio.file.Files.newByteChannel(path, localEnumSet, attr).close();
        OutputStream out = java.nio.file.Files.newOutputStream(path);
        return out;
    }

    private static FileAttribute<Set<PosixFilePermission>> getDefaultFileAttribute(
            File file, boolean isReadShare) {
        Path path = file.toPath();
        // File permissions should be such that only user may read/write file
        FileAttribute<?> fa = null;
        boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains(
                "posix");
        if ( isPosix ) { //linux 平台权限控制
            String permissons = isReadShare ? "rw-r-----" : "rw-------";
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissons);
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
            fa = attr;
        } else { // Windows 平台权限控制
            // for not posix must support ACL, or failed.
            String userName = System.getProperty("user.name");
            UserPrincipal user = null;
            try {
                user = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(
                        userName);
            } catch (IOException e) {
                LOGGER.error("GET Filesystem FAIL");
            }
            AclEntryPermission[] permList = new AclEntryPermission[] {
                    AclEntryPermission.READ_DATA,
                    AclEntryPermission.READ_ATTRIBUTES,
                    AclEntryPermission.READ_NAMED_ATTRS,
                    AclEntryPermission.READ_ACL, AclEntryPermission.WRITE_DATA,
                    AclEntryPermission.DELETE, AclEntryPermission.APPEND_DATA,
                    AclEntryPermission.WRITE_ATTRIBUTES,
                    AclEntryPermission.WRITE_NAMED_ATTRS,
                    AclEntryPermission.WRITE_ACL,
                    AclEntryPermission.SYNCHRONIZE};
            Set<AclEntryPermission> perms = EnumSet.noneOf(AclEntryPermission.class);
            for ( AclEntryPermission perm : permList )
                perms.add(perm);

            final AclEntry entry = AclEntry.newBuilder().setType(
                    AclEntryType.ALLOW).setPrincipal(user).setPermissions(perms).setFlags(
                    new AclEntryFlag[] {AclEntryFlag.FILE_INHERIT,
                            AclEntryFlag.DIRECTORY_INHERIT}).build();

            FileAttribute<List<AclEntry>> aclattrs = null;
            aclattrs = new FileAttribute<List<AclEntry>>() {
                public String name() {
                    return "acl:acl";
                } /* Windows ACL */

                public List<AclEntry> value() {
                    ArrayList<AclEntry> l = new ArrayList<AclEntry>();
                    l.add(entry);
                    return l;
                }
            };
            fa = aclattrs;
        }

        return (FileAttribute<Set<PosixFilePermission>>)fa;
    }


    /**
     * 作用： 创建文件夹
     * 输入：String 相对地址
     * 输出： void
     **/
    public static boolean makeFolder(String path)
    {
        String absolutePath = path;

        File dir = FileUtils.getFile(absolutePath);

        if (dir.exists()) {
            return false;
        }
        if (dir.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }

}
