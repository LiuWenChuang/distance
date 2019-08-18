package com.liuchuang.model.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@TableName(value = "sys_role")
public class Menu {
    /**
     权限编号
     */
    private Long id;
    /**
     * 权限名称
     */
    private String name;
    /**
     * 上级编号
     */
    private Long parentid;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 图标
     */
    private String icon;

    /**
     * url地址
     */
    private String href;
    /**
     * 权限
     */
    private String permission;
    /**
     * 是否显示
     */
    private Long isshow;

    /**
     * 创建时间,
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;
    /**
     * 修改时间,
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatetime;


}
