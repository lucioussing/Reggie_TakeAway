package com.it.reggie_takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie_takeaway.entity.OrderDetail;
import com.it.reggie_takeaway.mapper.OrderDetailMapper;
import com.it.reggie_takeaway.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
