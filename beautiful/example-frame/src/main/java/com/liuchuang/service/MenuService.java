package com.liuchuang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuchuang.model.core.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    List<Menu> getRoleMenuByRoleId(Long roleId);
}
