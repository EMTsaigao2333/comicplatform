package com.example.comicplatform_springbootweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 * @author 黑炎
 * @since 2026-07-05
 *
 * 二次修改需求：
 * 因为企业标准中用户对象不能包括password
 *这样 Jackson 在把 User 对象转 JSON 时，会自动忽略 password 字段。
 */
@Data// 等价 @Getter + @Setter + @ToString + @EqualsAndHashCode
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "userid", type = IdType.AUTO)
    private Integer userid;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     * 二次修改：
     * @JsonIgnore 防止密码被序列化到 JSON 返回给前端
     * 三次修改：原因，因为@JsonIgnore把密码给拦截了，后端接收的是null
     * WRITE_ONLY：注册/登录时允许接收密码，但序列化时绝不返回给前端
     */
    //@JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//WRITE_ONLY单向限制，只限制后端返回前端
    @TableField("password")
    private String password;

    /**
     * 用户类型
     */
    @TableField("usertype")
    private String usertype;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 创建日期
     */
    @TableField("createdate")
    private LocalDateTime createdate;
}
