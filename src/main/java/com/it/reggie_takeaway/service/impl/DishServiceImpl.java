package com.it.reggie_takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie_takeaway.dto.DishDto;
import com.it.reggie_takeaway.entity.Dish;
import com.it.reggie_takeaway.entity.DishFlavor;
import com.it.reggie_takeaway.mapper.DishMapper;
import com.it.reggie_takeaway.service.DishFlavorService;
import com.it.reggie_takeaway.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

	@Autowired
	private DishFlavorService dishFlavorService;

	/**
	 * 新增菜品保存方法
	 * 扩展方法，通过调用两个方法，分别给菜品dish和口味flavor两张表插入数据
	 * 两个方法，需要开事务
	 * @param dishDto   数据传输对象
	 */
	@Transactional
	public void saveWithFlavor(DishDto dishDto) {
		//1.保存菜品信息到菜品表
		this.save(dishDto);

		//2.保存口味信息到口味表(因为一个菜有多个口味,所以口味表数据对应了一个id
		//菜品id
		Long dishId = dishDto.getId();
		//口味(可以使用for循环
		List<DishFlavor> flavors = dishDto.getFlavors();
		/*
		stream为集合创建串行流
		map方法映射每一个元素对应结果，给每个元素都调用一次括号中的函数
		collect收集return的元素
		Collectors将流转换成集合
		 */
		flavors=flavors.stream().map((item) ->{
			item.setDishId(dishId);
			return item;
		}).collect(Collectors.toList());

		dishFlavorService.saveBatch(flavors);

	}

	/**
	 * 根据菜品id查询菜品和口味信息
	 * @param id   菜品id
	 * @return
	 */
	@Override
	public DishDto getByIdWithFlavor(Long id) {
		DishDto dishDto=new DishDto();
		//查询菜品信息
		Dish dish = this.getById(id);
		//查询口味信息
		LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.eq(DishFlavor::getDishId,id);
		List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

		//赋值
		BeanUtils.copyProperties(dish,dishDto);
		dishDto.setFlavors(flavors);

		return dishDto;
	}

	/**
	 * 更新菜品和口味表
	 * @param dishDto 传过来的信息
	 */
	@Override
	@Transactional
	public void updateWithFlavor(DishDto dishDto) {
		//更新Dish表信息
		this.updateById(dishDto);

		//清理当前口味表数据(根据dishId删除
		LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
		dishFlavorService.remove(queryWrapper);

		//添加当前口味表的数据,继续遍历每一个元素设置其dishId
		List<DishFlavor> flavors = dishDto.getFlavors();
		flavors=flavors.stream().map((item) ->{
			item.setDishId(dishDto.getId());
			return item;
		}).collect(Collectors.toList());
		dishFlavorService.saveBatch(flavors);

	}
}
