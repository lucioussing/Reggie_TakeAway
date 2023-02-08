package com.it.reggie_takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie_takeaway.common.R;
import com.it.reggie_takeaway.entity.Category;
import com.it.reggie_takeaway.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;


	/**
	 * 新增分类就三个参数，前端两个新增按钮可以共用一个方法,post提交
	 * @param category 分类
	 * @return
	 */
	@PostMapping
	public R<String> save(@RequestBody Category category){
		categoryService.save(category);
		return R.success("新增分类成功");
	}

	/**
	 * 分页查询
	 * @param page 第几页
	 * @param pageSize 一页几行
	 * @return  返回整个分页
	 */
	@GetMapping("/page")
	public R<Page> page(int page,int pageSize){
		//分页构造器
		Page pageInfo=new Page(page,pageSize);
		//条件构造器
		LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
		//进行排序条件
		queryWrapper.orderByAsc(Category::getSort);
		//调用service查询
		categoryService.page(pageInfo,queryWrapper);


		return  R.success(pageInfo);
	}

	/**
	 * 根据id删除菜分类,因为提交方式是delete所以能接收到了
	 * @param ids Uid参数要注意和前端?拼接传过来的是一样，要不然接收不到
	 * @return
	 */
	@DeleteMapping
	public R<String> delete(Long ids){
		/*
		需要看看这个分类是否关联了一个菜，要不然不能删
		不能直接用mp的方法了categoryService.removeById(ids);
		 */
		categoryService.remove(ids);

		return R.success("删除分类成功");
	}

	/**
	 * 根据id修改分类信息
	 * @param category json分类信息
	 * @return
	 */
	@PutMapping
	public R<String> update(@RequestBody Category category){
		/*
		回显表单不用做了，因为vue已经帮你回显了
		 */
		categoryService.updateById(category);
		return R.success("修改分类信息成功");
	}

	/**
	 * 给出菜品分类下拉框菜单,套餐也能用
	 * 这边type传进来的是1,因为这是查菜品不是套餐
	 * @param category 用来保存传进来的type，未来用来保存别的参数
	 * @return
	 */
	@GetMapping("/list")
	public R<List<Category>> list(Category category){
		LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
		//判断不为空，根据类型查询
		queryWrapper.eq(category.getType()!=null, Category::getType,category.getType());
		//根据排序升序，根据更新时间降序
		queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
		List<Category> list = categoryService.list(queryWrapper);
		return R.success(list);
	}
}
