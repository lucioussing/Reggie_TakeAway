package com.it.reggie_takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie_takeaway.entity.Category;


public interface CategoryService extends IService<Category> {
	public void remove(Long id);
}
