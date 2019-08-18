package com.liuchuang.model.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@TableName(value = "sys_role")
public class Role {
    /**
     角色编号
     */
    private Long id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 类型
     */
    private Long type;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 状态
     */
    private Long delflag;

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
