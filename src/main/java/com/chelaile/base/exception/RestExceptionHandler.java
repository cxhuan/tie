package com.chelaile.base.exception;

import com.chelaile.base.JResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@ControllerAdvice(annotations ={ RestController.class,RequestMapping.class})
public class RestExceptionHandler {
    private final static Logger elog = LogManager.getLogger("error");
	private final static Logger ilog = LogManager.getLogger("info");


	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	private JResponse runtimeExceptionHandler(Exception e) {
		elog.error("!!!runtime exception", e);

		JResponse jr = new JResponse();
		jr.setCode("9999");
		jr.setErrMsg(e.getMessage());
		
		return jr;
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	private JResponse businessExceptionHandler(BusinessException e) {
		ilog.error("!!!business error", e);

		JResponse jr = new JResponse();
		jr.setCode("9999");
		jr.setErrMsg(e.getMessage());

		return jr;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	private JResponse illegalParamsExceptionHandler(
			MethodArgumentNotValidException e) {
		elog.error("!!!invalid request!", e);
		JResponse jr = new JResponse();
		jr.setCode("9999");
		jr.setErrMsg(e.getMessage());
		return jr ;
	}
}
