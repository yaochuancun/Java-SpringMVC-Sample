package single.practice.utils;


/**
 * 执行http请求后的结果
 */
public class HttpRequestResult {
    // 请求的命令
    private String command;

    // 相应码
    private int responseCode;

    // 响应结果
    private String responseResult;

    // 响应cookie
    private String cookie;

    private String responseContentType;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean is201() {
        return (responseCode == 201);
    }

    public boolean is204() {
        return (responseCode == 204);
    }

    public boolean is409() {
        return (responseCode == 409);
    }

    public boolean isOk() {
        return (200 == responseCode);
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(String responseResult) {
        this.responseResult = responseResult;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    @Override
    public String toString() {
        return "HttpRequestResult [command=" + command + ", responseCode=" + responseCode + ", responseResult="
                + responseResult + ", cookie=" + cookie + ", responseContentType=" + responseContentType + "]";
    }
}
