package com.it.reggie_takeaway.controller;

import com.it.reggie_takeaway.common.R;
import com.it.reggie_takeaway.entity.Orders;
import com.it.reggie_takeaway.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

	@Autowired
	private OrderService orderService;


	/**
	 * 用户下单提交方法
	 * @param orders    订单数据
	 * @return
	 */
	@PostMapping("/submit")
	public R<String> submit(@RequestBody Orders orders){
		orderService.submit(orders);
		return R.success("下单成功");
	}
}
