package com.xm.reggie.dto;


import com.xm.reggie.entity.Setmeal;
import com.xm.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
