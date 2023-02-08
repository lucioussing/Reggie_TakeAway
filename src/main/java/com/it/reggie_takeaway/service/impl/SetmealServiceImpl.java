package com.it.reggie_takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie_takeaway.common.CustomException;
import com.it.reggie_takeaway.dto.SetmealDto;
import com.it.reggie_takeaway.entity.Setmeal;
import com.it.reggie_takeaway.entity.SetmealDish;
import com.it.reggie_takeaway.mapper.SetmealMapper;
import com.it.reggie_takeaway.service.SetmealDishService;
import com.it.reggie_takeaway.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


	@Autowired
	private SetmealDishService setmealDishService;

	/**
	 * 新增套餐，同时更新套餐与菜品关系
	 * @param setmealDto
	 */
	@Transactional
	public void saveWithDish(SetmealDto setmealDto) {
		//套餐基本信息，插入套餐基本信息
		this.save(setmealDto);

		//保存并更新关联关系表
		//发现在菜品套餐关系表中只有套餐ID没有设置，菜品id自动帮忙设置上了

		List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
		//赋不赋值回去其实无所谓的
		setmealDishes=setmealDishes.stream().map((item)->{
			item.setSetmealId(setmealDto.getId());
			return item;
		}).collect(Collectors.toList());

		//是因为这里在选择套餐的时候已经把菜品查询好了，并且把菜品ID保存了下来
		setmealDishService.saveBatch(setmealDishes);


	}

	/**
	 * 根据传过来参数删除套餐
	 * condition:停售状态才能删除
	 * @param ids   传过来id的集合
	 */
	@Override
	public void removeWithDish(List<Long> ids) {
		//先查询套餐中的status
		LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
		int count = this.count(queryWrapper);

		//不能删除则抛出异常
		if(count>0){
			throw new CustomException("售卖中套餐不能删除");
		}
		//如果可以删除，删除套餐表中数据(传进去的是集合
		this.removeByIds(ids);

		//删除套餐菜品关系表中数据
		LambdaQueryWrapper<SetmealDish> SDquery=new LambdaQueryWrapper<>();
		SDquery.in(SetmealDish::getSetmealId,ids);
		setmealDishService.remove(SDquery);//传进去的是条件



	}
}
