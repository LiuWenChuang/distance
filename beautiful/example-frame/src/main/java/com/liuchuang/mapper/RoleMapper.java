package com.liuchuang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuchuang.model.core.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> getRoleUserByUserId(Long userId);
}
