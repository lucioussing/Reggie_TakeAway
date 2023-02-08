package com.it.reggie_takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie_takeaway.dto.SetmealDto;
import com.it.reggie_takeaway.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
	public void saveWithDish(SetmealDto setmealDto);

	public void removeWithDish(List<Long> ids);
}
