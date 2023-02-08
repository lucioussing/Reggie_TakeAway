package com.it.reggie_takeaway.common;
/*
基于ThreadLocal封装的工具类
 */
public class BaseContext {
	/*
	用来保存当前用户的id
	同一个http请求会分配同一个线程处理，可以采取ThreadLocal来获取session
	 */
	private static ThreadLocal<Long> threadLocal =new ThreadLocal<>();

	public static void setThreadLocal(Long id){
		threadLocal.set(id);
	}
	public static Long getCunrrentId(){
		return threadLocal.get();
	}
}
