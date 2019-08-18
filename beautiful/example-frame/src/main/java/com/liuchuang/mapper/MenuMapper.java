package com.liuchuang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuchuang.model.core.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> getRoleMenuByRoleId(Long roleId);
}
