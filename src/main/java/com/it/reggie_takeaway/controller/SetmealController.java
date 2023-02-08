package com.it.reggie_takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie_takeaway.common.R;
import com.it.reggie_takeaway.dto.SetmealDto;
import com.it.reggie_takeaway.entity.Category;
import com.it.reggie_takeaway.entity.Setmeal;
import com.it.reggie_takeaway.service.CategoryService;
import com.it.reggie_takeaway.service.SetmealDishService;
import com.it.reggie_takeaway.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理，套餐与菜品关系管理也在这
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SetmealService setmealService;

	@Autowired
	private SetmealDishService setmealDishService;


	/**
	 * 新增套餐保存套餐和套餐菜品关系内容
	 * @param setmealDto    套餐和菜品内容
	 * @return
	 */
	@PostMapping
	public R<String> save(@RequestBody SetmealDto setmealDto){
		setmealService.saveWithDish(setmealDto);

		return R.success("新增套餐成功");
	}

	/**
	 * 套餐分页
	 * @param page  第几页
	 * @param pageSize  一页多大
	 * @param name  查询套餐模糊字段
	 * @return  分页对象
	 */
	@GetMapping("/page")
	public R<Page> page(int page,int pageSize,String name){
		//分页构造器
		Page<Setmeal> pageInfo=new Page<>(page,pageSize);
		Page<SetmealDto> dtoPage=new Page<>();
		//条件构造器
		LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
		queryWrapper.orderByDesc(Setmeal::getUpdateTime);

		setmealService.page(pageInfo,queryWrapper);


		//设置其categoryId
		BeanUtils.copyProperties(pageInfo,dtoPage,"records");
		List<Setmeal> record1 = pageInfo.getRecords();

		//处理原来的record
		List<SetmealDto> record2 = record1.stream().map((item) -> {
			SetmealDto setmealDto = new SetmealDto();
			//拷贝普通属性
			BeanUtils.copyProperties(item, setmealDto);

			//从原page里面拿到categoryId，然后从category里面查出来catename
			Long categoryId = item.getCategoryId();
			Category category = categoryService.getById(categoryId);
			if (category != null) {
				//装入Dto
				setmealDto.setCategoryName(category.getName());
			}
			return setmealDto;
		}).collect(Collectors.toList());

		//将处理好的Record装入dtoPage
		dtoPage.setRecords(record2);
		return R.success(dtoPage);
	}


	/**
	 * 删除套餐方法
	 * @param ids 套餐id
	 * @return
	 */
	@DeleteMapping
	public R<String> delete(@RequestParam List<Long> ids){
		setmealService.removeWithDish(ids);

		return R.success("套餐删除成功") ;
	}

	/**
	 * 前端用户页面查询套餐数据list
	 * @param setmeal   前端传过来的套餐信息
	 * @return
	 */
	@GetMapping("/list")
	public R<List<Setmeal>> list(Setmeal setmeal){
		LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
		queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
		queryWrapper.orderByDesc(Setmeal::getUpdateTime);
		List<Setmeal> setmealList = setmealService.list(queryWrapper);


		return R.success(setmealList);
	}
}
