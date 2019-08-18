package com.liuchuang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuchuang.model.core.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<Role> getRoleUserByUserId(Long userId);
}
