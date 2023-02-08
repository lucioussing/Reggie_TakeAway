package com.it.reggie_takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie_takeaway.common.CustomException;
import com.it.reggie_takeaway.entity.Category;
import com.it.reggie_takeaway.entity.Dish;
import com.it.reggie_takeaway.entity.Setmeal;
import com.it.reggie_takeaway.mapper.CategoryMapper;
import com.it.reggie_takeaway.service.CategoryService;
import com.it.reggie_takeaway.service.DishService;
import com.it.reggie_takeaway.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

	@Autowired
	private DishService dishService;

	@Autowired
	private SetmealService setmealService;

	/**
	 * 根据id删除分类，删除之前要进行判断
	 *
	 * @param id Uid
	 */
	@Override
	public void remove(Long id) {
		//查询当前分类是否关联了菜品，关联即抛出异常
		//定义条件构造器，找出符合id的菜品数量
		LambdaQueryWrapper<Dish> dishQueryWrapper=new LambdaQueryWrapper<>();
		dishQueryWrapper.eq(Dish::getCategoryId,id);
		int count1 = dishService.count(dishQueryWrapper);
		if (count1!=0){
			throw new CustomException("关联了菜品不能删除哦！");
		}
		//查询当前分类是否关联了套餐，关联即抛出异常
		LambdaQueryWrapper<Setmeal> SetmealQueryWrapper=new LambdaQueryWrapper<>();
		SetmealQueryWrapper.eq(Setmeal::getCategoryId,id);
		int count2 = setmealService.count(SetmealQueryWrapper);
		if (count2!=0){
			throw new CustomException("关联了套餐不能删除哦！");
		}
		//正常删除(super调用父类方法
		super.removeById(id);
	}
}
