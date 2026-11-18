package com.example.comicplatform_springbootweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 漫画分类实体类
 * 用于维护平台漫画题材分类（热血、冒险、恋爱等）
 */
@TableName("t_category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID（主键，数据库自增）
     */
    @TableId(value = "categoryid", type = IdType.AUTO)
    private Integer categoryid;

    /**
     * 分类名称
     */
    @TableField("categoryname")
    private String categoryname;

    /**
     * 分类描述
     */
    @TableField("description")
    private String description;

    /**
     * 排序号，数字越小越靠前
     */
    @TableField("sortorder")
    private Integer sortorder;

    /**
     * 分类状态：启用/停用
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField("createtime")
    private LocalDateTime createtime;

    public Integer getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Integer categoryid) {
        this.categoryid = categoryid;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortorder() {
        return sortorder;
    }

    public void setSortorder(Integer sortorder) {
        this.sortorder = sortorder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }
}
