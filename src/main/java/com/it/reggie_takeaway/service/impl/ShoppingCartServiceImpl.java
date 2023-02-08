package com.it.reggie_takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie_takeaway.entity.ShoppingCart;
import com.it.reggie_takeaway.mapper.ShoppingCartMapper;
import com.it.reggie_takeaway.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
