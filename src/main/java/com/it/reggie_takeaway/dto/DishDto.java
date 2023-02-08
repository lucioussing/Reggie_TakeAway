package com.it.reggie_takeaway.dto;


import com.it.reggie_takeaway.entity.Dish;
import com.it.reggie_takeaway.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    /**
     * Dto data transfer object
     * 1.继承了Dish，子类获得dish实体类所有属性
     * 2.定义一个list封装flavor口味
     */
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
