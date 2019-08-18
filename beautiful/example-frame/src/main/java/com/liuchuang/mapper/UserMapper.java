package com.liuchuang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuchuang.model.core.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
