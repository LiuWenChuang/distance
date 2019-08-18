package com.liuchuang.service.iml;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuchuang.mapper.RoleMapper;
import com.liuchuang.model.core.entity.Role;
import com.liuchuang.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService{


    @Override
    public List<Role> getRoleUserByUserId(Long userId) {
        return baseMapper.getRoleUserByUserId(userId);
    }
}
