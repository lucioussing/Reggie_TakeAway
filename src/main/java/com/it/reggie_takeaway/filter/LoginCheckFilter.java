package com.it.reggie_takeaway.filter;

import com.alibaba.fastjson.JSON;
import com.it.reggie_takeaway.common.BaseContext;
import com.it.reggie_takeaway.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
检查用户登录过滤器，通配会把所有的路径请求全部抓过来
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

	//路径匹配器
	public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;


		//1、获取本次请求的URI
		String requestURI = request.getRequestURI();

		String[] urIs = new String[]{//控制放行的URI
				"/employee/login",
				"/employee/logout",
				"/backend/**",
				"/front/**",
				"/common/**",
				"/user/sendMsg",//移动端发送短信
				"/user/login"//移动端的登录
		};
		//2、判断本次请求是否需要处理
		boolean check = check(urIs, requestURI);

		//3、如果不需要处理，则直接放行
		if (check) {
			filterChain.doFilter(request, response);
			return;
		}
		//4_1、判断登录状态，如果已登录，则直接放行
		if (request.getSession().getAttribute("employee") != null) {
			//将id存进threadLocal
			Long empId = (Long) request.getSession().getAttribute("employee");
			BaseContext.setThreadLocal(empId);

			filterChain.doFilter(request, response);
			return;
		}
		//4_2、判断登录状态，如果已登录，则直接放行
		if (request.getSession().getAttribute("user") != null) {
			//将id存进threadLocal
			Long userId = (Long) request.getSession().getAttribute("user");
			BaseContext.setThreadLocal(userId);

			filterChain.doFilter(request, response);
			return;
		}
		//5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
		response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));


	}

	/*
	进行路径匹配，检查本次请求是否需要放行，将本次URI和可放行的URI传进来进行匹配
	 */
	public boolean check(String[] urIs, String requestURI) {
		for (String url :
				urIs) {
			boolean match = PATH_MATCHER.match(url, requestURI);
			if (match) {
				return true;
			}
		}
		return false;
	}

}
