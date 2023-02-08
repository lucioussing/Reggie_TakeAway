package com.it.reggie_takeaway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.reggie_takeaway.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
