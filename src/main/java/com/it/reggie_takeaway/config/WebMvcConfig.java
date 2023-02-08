package com.it.reggie_takeaway.config;

import com.it.reggie_takeaway.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
	/*
	静态资源映射
	 */


	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info("开始静态资源映射");
		//url通配映射到对应文件夹下面
		registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
		registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
	}

	/**
	 * 扩展mvc框架的消息转换器
	 * 前端传过来的消息自动拦截并且转换
	 * 因为传过来的id是有问题的，所以要手动改变
	 * @param converters
	 */
	@Override
	protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		//创建消息转换器对象
		MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
		//设置对象转换器，将那个可以序列化反序列化的方法new进来
		converter.setObjectMapper(new JacksonObjectMapper());
		//将上面的消息转换器对象追加到mvc框架的转换器集合里
		converters.add(0,converter);
	}
}
