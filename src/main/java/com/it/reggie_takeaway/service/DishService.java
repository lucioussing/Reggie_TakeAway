package com.it.reggie_takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie_takeaway.dto.DishDto;
import com.it.reggie_takeaway.entity.Dish;

public interface DishService extends IService<Dish> {

	public void saveWithFlavor(DishDto dishDto);

	public DishDto getByIdWithFlavor(Long id);

	public void updateWithFlavor(DishDto dishDto);
}
