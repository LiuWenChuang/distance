package com.liuchuang.service.iml;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuchuang.mapper.MenuMapper;
import com.liuchuang.model.core.entity.Menu;
import com.liuchuang.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Override
    public List<Menu> getRoleMenuByRoleId(Long roleId) {
        return baseMapper.getRoleMenuByRoleId(roleId);
    }
}
