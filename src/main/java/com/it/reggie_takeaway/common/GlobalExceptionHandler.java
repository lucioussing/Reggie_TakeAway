package com.it.reggie_takeaway.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/*
全局异常处理器
底层通过aop代理controller，拦截这些处理，需标明controller的注解
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

	/*
	声明自己想要处理的异常
	 */
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){

		if (ex.getMessage().contains("Duplicate entry")){
			String[] s = ex.getMessage().split(" ");
			String msg = s[2] + "已存在";
			return R.error(msg);
		}
		return R.error("未知错误，失败了");
	}

	/*
	对自己定义的异常信息捕获
	 */
	@ExceptionHandler(CustomException.class)
	public R<String> exceptionHandler(CustomException ce){
		return R.error(ce.getMessage());//利用异常类的getMess方法获取字符串
	}


}
