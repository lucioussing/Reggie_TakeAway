package com.it.reggie_takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie_takeaway.entity.DishFlavor;
import com.it.reggie_takeaway.mapper.DishFlavorMapper;
import com.it.reggie_takeaway.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
