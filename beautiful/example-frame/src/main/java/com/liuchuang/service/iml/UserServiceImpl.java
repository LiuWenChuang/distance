package com.liuchuang.service.iml;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuchuang.mapper.UserMapper;
import com.liuchuang.model.core.entity.User;
import com.liuchuang.service.Userservice;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements Userservice {

}
