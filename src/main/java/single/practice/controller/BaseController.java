package single.practice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import single.practice.constant.ErrorCodeConstants;
import single.practice.util.ErrorUtil;
import single.practice.utils.JSONHelper;

@RestController
public class BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public String handleException(Exception e) {

		LOGGER.error("runtime exception",e);
		return JSONHelper.response2String(ErrorUtil.getErrorResponse(ErrorCodeConstants.INTERNAL_ERROR));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public String handleRequestException(HttpMessageNotReadableException e) {

		LOGGER.error("runtime exception",e);
		return JSONHelper.response2String(ErrorUtil.getErrorResponse(ErrorCodeConstants.BAD_REQUEST));
	}
}
