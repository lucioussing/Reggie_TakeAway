package com.it.reggie_takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.reggie_takeaway.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
