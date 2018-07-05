package single.practice.filter.filter;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import single.practice.beans.response.Response;
import single.practice.constant.ErrorCodeConstants;
import single.practice.util.ErrorUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;


public class TokenFilter implements HandlerInterceptor {


    Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);
    private static final String REQUEST_USER = "user";
    private static final String REQUEST_USER_ID = "user_id";
    private static final String REQUEST_DOMAIN = "domain";
    private static final String REQUEST_DOMAIN_ID = "domain_id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        String pkiToken = request.getHeader("X-Auth-Token");
        try {
             responseObj = DevCloudTokenStore.getTokenInfo(pkiToken, ConfigUtil.getProperty("IAM_TOKE_URL"));
        } catch (NullPointerException  | IllegalArgumentException e) {
            //IAMtoken或者其他
            responseObj = DevCloudTokenStore.getTokenInfo(pkiToken,ConfigUtil.getProperty("IAM_TOKE_URL"));
            if (null != responseObj && Response.SUCCESS.equals(responseObj.getStatus())) {
                LOGGER.info("get token info failed");
            }
        }
        if (responseObj!=null && Response.SUCCESS.equals(responseObj.getStatus())) {
            String json = gson.toJson(responseObj.getResult());
            Map<String, String> map = gson.fromJson(json, Map.class);

            if (map.containsKey("tokenInfo")) {
                DevCloudTokenStore.tokenInfo.set(map.get("tokenInfo"));

                DevCloudTokenStore.analyzeToken(); // 存储token中的projectId（区域id）

                DevCloudTokenStore.iamToken.set(pkiToken);
                DevCloudTokenStore.token.set(DevCloudTokenStore.getCustomToken(DevCloudTokenStore.getUserId(), DevCloudTokenStore.getUserName(),
                            DevCloudTokenStore.getDomainId(), DevCloudTokenStore.getDomainName()));

                if (StringUtils.endsWith(DevCloudTokenStore.getUserName(), "op_service")) {
                    String userName = request.getHeader(REQUEST_USER);
                    String userId = request.getHeader(REQUEST_USER_ID);
                    String domain = request.getHeader(REQUEST_DOMAIN);
                    String domainId = request.getHeader(REQUEST_DOMAIN_ID);
                    setThreadLocalHeaderInfo(userId, userName, domainId, domain);
                }
                return true;
            } else {
                LOGGER.error("method:preHandle error occurred. message:iamToken is null.");
                PrintWriter printWriter = response.getWriter();
                Response errorResponse = null;
                errorResponse = ErrorUtil.getErrorResponse(ErrorCodeConstants.INTERNAL_ERROR);
                printWriter.write(this.gson.toJson(errorResponse));
                return false;
            }
        }
        LOGGER.error("res tokenInfo is request iam token failed ,please check iam url or iamToken");
        LOGGER.error("method:preHandle error occurred. message:iamToken is null or error.");
        PrintWriter printWriter = response.getWriter();
        Response errorResponse = null;
        errorResponse = ErrorUtil.getErrorResponse(ErrorCodeConstants.INVALID_TOKEN);
        printWriter.write(this.gson.toJson(errorResponse));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

    private void setThreadLocalHeaderInfo(String userId, String userName, String domainId, String domainName) {
        if (StringUtils.isNotEmpty(userId)) {
            DevCloudTokenStore.id.set(userId);
        }

        if (StringUtils.isNotEmpty(userName)) {
            DevCloudTokenStore.username.set(userName);
        }

        if (StringUtils.isNotEmpty(domainId)) {
            DevCloudTokenStore.domainid.set(domainId);
        }

        if (StringUtils.isNotEmpty(domainName)) {
            DevCloudTokenStore.domain.set(domainName);
        }

    }
}
