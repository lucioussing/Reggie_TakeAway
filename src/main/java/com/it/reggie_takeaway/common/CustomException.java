package com.it.reggie_takeaway.common;
/*
自定义异常类
 */
public class CustomException extends RuntimeException{
	public CustomException (String message){
		super(message);//异常显示传进来的message
	}
}
