package com.it.reggie_takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie_takeaway.common.R;
import com.it.reggie_takeaway.entity.User;
import com.it.reggie_takeaway.service.UserService;
import com.it.reggie_takeaway.utils.SMSUtils;
import com.it.reggie_takeaway.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/*
user用户controller
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;


	/**
	 * 手机短信验证码发送
	 *
	 * @param user 用户实体存手机号
	 * @return
	 */
	@PostMapping("/sendMsg")
	public R<String> sendMess(@RequestBody User user, HttpSession session) {
		//获取手机号
		String phone = user.getPhone();
		if (StringUtils.isNotEmpty(phone)) {

			//生成随机四位验证码
			String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
			log.info("code={}",code);
			//调用阿里云验证码服务发送
//			SMSUtils.sendMessage("ProjectTest", "SMS_267060435", phone, code);

			//保存生成的验证码到Session
			session.setAttribute(phone,code);
			return R.success("手机验证码发送成功");
		}
		return R.error("验证码发送失败");

	}


	/**
	 * 移动端用户登录
	 *
	 * @param map     存储phone&code
	 * @param session
	 * @return
	 */
	@PostMapping("/login")
	public R<User> login(@RequestBody Map map, HttpSession session) {
		//map中获取手机号
		String phone = map.get("phone").toString();
		log.info(phone);
		//获取验证码
		String code = map.get("code").toString();
		log.info(code);
		//获取session中保存的验证码
		String codeInSession = session.getAttribute(phone).toString();
		log.info(codeInSession);
		//比对验证码
		if (codeInSession != null && codeInSession.equals(code)) {
			//比对成功，就登录成功了
			//判断当前手机号是否是新用户，新，则自动注册
			LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
			queryWrapper.eq(User::getPhone,phone);
			User user = userService.getOne(queryWrapper);
			if (user==null){
				user=new User();
				user.setPhone(phone);
				user.setStatus(1);
				userService.save(user);
			}
			session.setAttribute("user",user.getId());
			return R.success(user);
		}
		return R.error("登录失败");

	}
}
