package com.it.reggie_takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie_takeaway.common.BaseContext;
import com.it.reggie_takeaway.common.CustomException;
import com.it.reggie_takeaway.entity.*;
import com.it.reggie_takeaway.mapper.OrderMapper;
import com.it.reggie_takeaway.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {


	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private UserService userService;

	@Autowired
	private AddressBookService addressBookService;

	@Autowired
	private OrderDetailService orderDetailService;

	/**
	 * 用户下单
	 *
	 * @param orders 用户订单信息
	 */
	@Transactional
	public void submit(Orders orders) {
		//获得当前用户id
		Long userId = BaseContext.getCunrrentId();

		//根据当前Uid查询当前用户购物车信息
		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, userId);
		List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

		if (shoppingCarts == null || shoppingCarts.size() == 0) {
			throw new CustomException("购物车为空，不能下单");
		}
		//查询用户信息
		User user = userService.getById(userId);
		if (user == null) {
			throw new CustomException("用户信息有误，不能下单");
		}

		//查询地址信息
		Long addressBookId = orders.getAddressBookId();
		AddressBook addressBook = addressBookService.getById(addressBookId);
		if (addressBook == null) {
			throw new CustomException("用户地址信息有误，不能下单");
		}


		//向orders表插入数据 (除了提交过来的三个参数其他的都得设置
		long orderId = IdWorker.getId();//订单号

		//遍历订单详情，并设置其数值
		AtomicInteger amount = new AtomicInteger(0);//遍历时累加值
		List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrderId(orderId);
			orderDetail.setNumber(item.getNumber());
			orderDetail.setDishFlavor(item.getDishFlavor());
			orderDetail.setDishId(item.getDishId());
			orderDetail.setSetmealId(item.getSetmealId());
			orderDetail.setName(item.getName());
			orderDetail.setImage(item.getImage());
			orderDetail.setAmount(item.getAmount());
			amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
			return orderDetail;
		}).collect(Collectors.toList());


		//设置订单信息
		orders.setId(orderId);
		orders.setOrderTime(LocalDateTime.now());
		orders.setCheckoutTime(LocalDateTime.now());
		orders.setStatus(2);//2代表待派送
		orders.setAmount(new BigDecimal(amount.get()));//总金额
		orders.setUserId(userId);
		orders.setNumber(String.valueOf(orderId));
		orders.setUserName(user.getName());
		orders.setConsignee(addressBook.getConsignee());
		orders.setPhone(addressBook.getPhone());
		orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
				+ (addressBook.getCityName() == null ? "" : addressBook.getCityName())
				+ (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
				+ (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

		this.save(orders);


		//向order_detail表插入数据
		orderDetailService.saveBatch(orderDetails);

		//清空购物车数据
		shoppingCartService.remove(queryWrapper);
	}
}
