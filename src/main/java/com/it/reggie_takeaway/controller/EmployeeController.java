package com.it.reggie_takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie_takeaway.common.R;
import com.it.reggie_takeaway.entity.Employee;
import com.it.reggie_takeaway.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/*
	员工登录
	因为前端发送的是post请求,前端传过来的是json对象要加requestBody,
	request获取session，把用户登录存进去
	 */
	@PostMapping("/login")
	public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {


		//1、将页面提交的密码password进行md5加密处理,DigestUtils是自带的md5加密
		String password = employee.getPassword();

		password = DigestUtils.md5DigestAsHex(password.getBytes());

		//2、根据页面提交的用户名username查询数据库，lambda是mybatis p的用法
		//前面一个是实体类列名，后面一个是传进来的参数用户名
		LambdaQueryWrapper<Employee> QueryWrapper = new LambdaQueryWrapper<>();
		QueryWrapper.eq(Employee::getUsername, employee.getUsername());
		Employee emp = employeeService.getOne(QueryWrapper);
		//查出来一个唯一的数据，getOne方法是mybatis p的方法，真的很强大

		//3、如果没有查询到则返回登录失败结果
		if (emp == null) {
			return R.error("登录失败");
		}

		//4、密码比对，比对md5加密之后的密码,如果不一致则返回登录失败结果
		//前一个是通过用户名查出来的用户的数据库的密码，后一个是前端传进来的密码md5加密的密码
		if (!emp.getPassword().equals(password)) {
			return R.error("登录失败");
		}

		//5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
		if (emp.getStatus() == 0) {
			return R.error("账号已经被禁用");
		}

		//6、登录成功，将员工id存入Session并返回登录成功结果
		request.getSession().setAttribute("employee", emp.getId());

		return R.success(emp);
	}

	/*
	员工退出
	 */
	@PostMapping("/logout")
	public R<String> logOut(HttpServletRequest request) {

		//清理员工登录保存的session
		request.getSession().removeAttribute("employee");

		return R.success("退出成功");
	}

	/*
	新增员工
	请求地址是employee，类上已经加了，这里不用加了
	 */
	@PostMapping
	public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
		//给个默认md5加密的密码123456
		employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

//		employee.setCreateTime(LocalDateTime.now());
//		employee.setUpdateTime(LocalDateTime.now());

		//这个是设置者ID
//		Long empId = (Long) request.getSession().getAttribute("employee");
//		employee.setCreateUser(empId);
//		employee.setUpdateUser(empId);


		employeeService.save(employee);
		return R.success("新增员工成功");
	}

	/*
	分页查询，泛型页面需要page我们传mybatis的page进去
	由于传过来的不是Json数据不需要用employee接收，url直接声明参数即可
	 */
	@GetMapping("/page")
	public R<Page> page(int page, int pageSize, String name) {
		//构造分页构造器mp的，传入第几页一页几条,page里面包含整条数据所有信息
		Page pageInfo=new Page(page,pageSize);
		//构造条件构造器也是mp的
		LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
		//添加过滤条件，需要判断name是否为空，空了就不同执行了，下面这个是lang包底下的
		queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
		//排序条件
		queryWrapper.orderByDesc(Employee::getUpdateTime);
		//执行查询，根据wrapper过滤条件查询记录并翻页
		employeeService.page(pageInfo,queryWrapper);

		return R.success(pageInfo);
	}

	/**
	 * 注：因为js修改了id，需要对id做消息过滤器
	 * 根据id修改员工信息(更新状态或者修改员工信息皆可使用
	 * 下面那个修改员工信息也是用这个方法，提交方式都是put和上面的新建save方法提交方式不一样
	 * @param employee 前端传过来id和status
	 * @return
	 */
	@PutMapping
	public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
		//要修改这个用户的更新时间和更新用户
//		Long id = (Long) request.getSession().getAttribute("employee");
//		employee.setUpdateTime(LocalDateTime.now());
//		employee.setUpdateUser(id);
		employeeService.updateById(employee);

		return R.success("状态更新成功");
	}

	/**
	 * 标记请求url传过来的id，根据id查询员工
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")//居然还能这样标注
	public R<Employee> getByd(@PathVariable Long id){
		Employee emp = employeeService.getById(id);
		if (emp!=null){
			return  R.success(emp);
		}
		return R.error("查询失败");
	}
}
