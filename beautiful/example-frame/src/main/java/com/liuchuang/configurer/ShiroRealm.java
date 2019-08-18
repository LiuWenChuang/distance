package com.liuchuang.configurer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuchuang.model.core.entity.Menu;
import com.liuchuang.model.core.entity.Role;
import com.liuchuang.model.core.entity.User;
import com.liuchuang.service.MenuService;
import com.liuchuang.service.RoleService;
import com.liuchuang.service.Userservice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * 验证用户账户名密码以及设置对应权限
 */
@Slf4j
public class ShiroRealm extends AuthorizingRealm {
    @Autowired
    private Userservice userservice;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Object primaryPrincipal = principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if(primaryPrincipal instanceof  User){
           User user =  (User)primaryPrincipal;
           if(user != null){
               List<Role> roles = roleService.getRoleUserByUserId(user.getUserid());
               for (Role role : roles) {
                    info.addRole(role.getName());
                   List<Menu> menus = menuService.getRoleMenuByRoleId(role.getId());
                   if(CollectionUtils.isNotEmpty(menus)){
                       for (Menu menu : menus) {
                           info.addStringPermission(menu.getPermission());
                       }
                   }
               }
           }
        }
        return null;
    }

    /**
     * 认证信息.(身份验证) : Authentication 是用来验证用户身份
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String username = usernamePasswordToken.getUsername();
        User user = userservice.getOne(new QueryWrapper<User>().eq("status", 1).eq("username", username));
        if (user != null) {
            // 用户为禁用状态
            if (!user.getLoginflag().equals("1")) {
                throw new DisabledAccountException();
            }
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, user.getPasswrod(), getName());
            return authenticationInfo;
        }
        throw new UnknownAccountException();
    }
}
