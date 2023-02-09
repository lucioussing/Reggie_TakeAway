package com.it.reggie_takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie_takeaway.common.R;
import com.it.reggie_takeaway.dto.DishDto;
import com.it.reggie_takeaway.entity.Category;
import com.it.reggie_takeaway.entity.Dish;
import com.it.reggie_takeaway.entity.DishFlavor;
import com.it.reggie_takeaway.service.CategoryService;
import com.it.reggie_takeaway.service.DishFlavorService;
import com.it.reggie_takeaway.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品dish和菜品口味dishFlavor都在这
 */
@RestController
@RequestMapping("/dish")
public class DishController {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private DishService dishService;

	@Autowired
	private DishFlavorService dishFlavorService;

	@Autowired
	private RedisTemplate redisTemplate;


	/**
	 * 保存新增菜品方法
	 * @param dishDto data transfer object数据传输对象，封装了dish和flavor
	 * @return
	 */
	@PostMapping
	public R<String> save(@RequestBody DishDto dishDto){
		dishService.saveWithFlavor(dishDto);

		//清理某个分类下面的缓存
		String key="dish_"+dishDto.getCategoryId()+"_1";
		redisTemplate.delete(key);

		return R.success("新增菜品成功");
	}

	/**
	 * 菜品dish分页查询
	 * @param page  第几页
	 * @param pageSize  每页多大
	 * @param name  菜品名称(搜索了分页才会有这个name
	 * @return  分页对象
	 */
	@GetMapping("/page")
	public R<Page> page(int page,int pageSize,String name){
		//分页构造器(将普通类型page改造成dishDto的类型
		Page<Dish> pageInfo=new Page<>(page,pageSize);
		Page<DishDto> dishDtoPage=new Page<>();
		//条件构造器
		LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
		//条件
		queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
		queryWrapper.orderByDesc(Dish::getUpdateTime);
		//查询dish的分页
		dishService.page(pageInfo, queryWrapper);



		//将dish的page拷贝到dishDto的page,   因为record需要处理所以不拷贝
		BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

		//将需要处理的record拿出来
		List<Dish> records = pageInfo.getRecords();

		/*
		 * 第一步：将普通page的普通属性拷过来
		 * 第二步：将普通record(Dish里面的属性全部拷到record(DishDto
		 * 第三步：将查出来的cateName存进去
		 * record进行处理:
		 * 1.page的record里存储的是数据库查出来的记录，将普通dish的记录全部copy给Dto
		 * 2.item就是stream形式的record,里面的存储的cateId拿出来，
		 * 通过cateId查询category表获取cateName
		 * 3.Dto类里面有个字段专门用来存储这个cateName
		 */
		List<DishDto> list=records.stream().map((item)->{
			DishDto dishDto=new DishDto();
			//1.
			BeanUtils.copyProperties(item,dishDto);

			//2.
			Long categoryId = item.getCategoryId();//分类id
			Category category = categoryService.getById(categoryId);
			//3.
			if (category!=null){
				String cateName = category.getName();
				dishDto.setCategoryName(cateName);
			}
			return dishDto;
		}).collect(Collectors.toList());

		//将查好cateName的List[] record  也就是处理好的record存入dishDto的page
		dishDtoPage.setRecords(list);


		return R.success(dishDtoPage);
	}

	/**
	 * 根据菜品id查询菜品和口味信息
	 * @param id 菜品id
	 * @return  dish+flavor
	 */
	@GetMapping("/{id}")
	public R<DishDto> get(@PathVariable Long id){

		DishDto dishDto = dishService.getByIdWithFlavor(id);
		return R.success(dishDto);
	}

	/**
	 * 修改菜品信息(更新
	 * @param dishDto   修改的信息
	 * @return
	 */
	@PutMapping
	public R<String> update(@RequestBody DishDto dishDto){
		dishService.updateWithFlavor(dishDto);
		/*
		因为新增菜品之后菜品进入了数据库，如果缓存不清理，list方法能直接在
		redis中查询到菜品，所以不会查数据库，导致新增菜品不显示
		(所以需要插入之后清除缓存
		 */

		//清理某个分类下面的缓存
		String key="dish_"+dishDto.getCategoryId()+"_1";
		redisTemplate.delete(key);

		return R.success("修改菜品成功");
	}

	/**
	 * 希望通过传过来的参数，返回菜品(category_id,状态为禁售就不查了
	 * @param dish  提高复用性传过来dish
	 * @return
	 */
//	@GetMapping("/list")
//	public R<List<Dish>> list(Dish dish){
//		//条件构造器
//		LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//		queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//		queryWrapper.eq(Dish::getStatus,1);
//		queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//		List<Dish> list = dishService.list(queryWrapper);
//
//		return R.success(list);
//	}

	/**
	 * 这个是个列表list，不是分页page，需要在用户端返回口味数据，所以需要改造
	 * 原先的不查口味后端也能用，因为只是追加了个口味
	 * @param dish
	 * @return
	 */
	@GetMapping("/list")
	public R<List<DishDto>> list(Dish dish){
		/*
		1.菜品数据先从redis中获取数据
		2.如果存在则直接获取
		3.不存在则从数据库获取，并且存入缓存中
		（key从哪获得
		 */

		//查询结果
		List<DishDto> dishDtos=null;

		//动态拼接key
		String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();

		//从redis中获取数据
		dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);

		if (dishDtos!=null){
			//如果存在则直接获取
			return R.success(dishDtos);
		}

		//条件构造器
		LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
		queryWrapper.eq(Dish::getStatus,1);
		queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

		List<Dish> list = dishService.list(queryWrapper);

		/*
		 * 上面复制的方法
		 */
		dishDtos=list.stream().map((item)->{
			DishDto dishDto=new DishDto();
			//1.
			BeanUtils.copyProperties(item,dishDto);

			//2.
			Long categoryId = item.getCategoryId();//分类id
			Category category = categoryService.getById(categoryId);
			//3.
			if (category!=null){
				String cateName = category.getName();
				dishDto.setCategoryName(cateName);
			}

			//对口味数据进行处理
			Long dishId = item.getId();
			LambdaQueryWrapper<DishFlavor> queryWrapperFlavor=new LambdaQueryWrapper<>();
			queryWrapperFlavor.eq(DishFlavor::getDishId,dishId);
			List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapperFlavor);

			dishDto.setFlavors(dishFlavors);


			return dishDto;
		}).collect(Collectors.toList());

		//不存在则从数据库获取，并且存入缓存中
		redisTemplate.opsForValue().set(key,dishDtos,60, TimeUnit.MINUTES);

		return R.success(dishDtos);
	}
}
