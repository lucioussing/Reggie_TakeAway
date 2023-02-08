package com.it.reggie_takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie_takeaway.entity.Orders;

public interface OrderService extends IService<Orders> {
	public void submit(Orders orders);
}
