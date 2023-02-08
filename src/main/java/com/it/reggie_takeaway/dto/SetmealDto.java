package com.it.reggie_takeaway.dto;

import com.it.reggie_takeaway.entity.Setmeal;
import com.it.reggie_takeaway.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
