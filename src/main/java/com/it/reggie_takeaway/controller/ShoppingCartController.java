package com.it.reggie_takeaway.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie_takeaway.common.BaseContext;
import com.it.reggie_takeaway.common.R;
import com.it.reggie_takeaway.entity.ShoppingCart;
import com.it.reggie_takeaway.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;


	/**
	 * 添加购物车方法(根据具体传过来的套餐或菜品Id添加
	 *
	 * @param shoppingCart 购物车数据
	 * @return
	 */
	@PostMapping("/add")
	public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
		//设置用户ID
		Long userId = BaseContext.getCunrrentId();
		shoppingCart.setUserId(userId);

		//查询当前这个菜品或者套餐是否在购物车中
		Long dishId = shoppingCart.getDishId();
		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, userId);//购物车中userId相等

		if (dishId != null) {
			//添加的是菜品，比对dishId
			queryWrapper.eq(ShoppingCart::getDishId, dishId);

		} else {
			//添加的是套餐，对比setmealId
			Long setmealId = shoppingCart.getSetmealId();
			queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);

		}
		ShoppingCart cart = shoppingCartService.getOne(queryWrapper);

		if (cart != null) {
			//存在则在cart原来基础数量上加一
			Integer number = cart.getNumber();
			cart.setNumber(number+1);
			cart.setCreateTime(LocalDateTime.now());
			shoppingCartService.updateById(cart);
		}else {
			//不存在则用shoppingCart加一，数量默认一
			shoppingCart.setNumber(1);
			shoppingCart.setCreateTime(LocalDateTime.now());
			shoppingCartService.save(shoppingCart);
			cart=shoppingCart;
			//cart和shoppingCart和上面那个if统一
		}

		return R.success(cart);
	}

	/**
	 * 查看购物车方法(根据用户id
	 * @return
	 */
	@GetMapping("/list")
	public R<List<ShoppingCart>> list(){
		LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCunrrentId());
		queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

		List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

		return R.success(list);
	}

	/**
	 * 清空购物车
	 * @return
	 */
	@DeleteMapping("/clean")
	public R<String> clean(){
		LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCunrrentId());

		shoppingCartService.remove(queryWrapper);

		return R.success("清空购物车成功");
	}
}
