package com.liuchuang.model.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@TableName(value = "sys_user")
public class User {
    /**
     * 用户编号,
     */
    private Long   userid;
    /**
     *  用户名,
     */
    private String   username;
    /**
     *  登录名,
     */
    private String    loginname;
    /**
     * 密码,
     */
    private String   passwrod;
    /**
     *   登录时间,
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logindate;
    /**
     *  登录IP,
     */
    private String loginip;
    /**
     *  登录状态  1正常 2下线,
     */
    private Long    loginflag;
    /**
     * 状态 1 正常  2 限制登录 88 删除  ,
     */
    private Long   status;
    /**
     *   创建时间,
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime  createtime;
    /**
     * 修改时间,
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatetime;
}
