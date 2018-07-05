//package single.practice.util;
//
//import org.springframework.context.NoSuchMessageException;
//import single.practice.beans.response.Response;
//import single.practice.resources.ResourceLoader;
//
///**
// * 统一错误获取接口
// */
//public abstract class ErrorUtil {
//	public static Error getError(String errorCode) {
//		try {
//			String reason = ResourceLoader.getResourceLoader().loadValue(errorCode, null);
//			return new Error(errorCode, reason);
//		} catch (NoSuchMessageException e) {
//			return new Error("DEV-16-50000", String.format("遇到未知错误,%s",errorCode));
//		}
//	}
//
//	  public static Response getErrorResponse(String errorCode)
//	    {
//	        Response response = new Response();
//	        response.setError(getError(errorCode));
//		return response;
//	}
//}
