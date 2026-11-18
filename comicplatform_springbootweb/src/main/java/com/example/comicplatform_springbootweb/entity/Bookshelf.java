package com.example.comicplatform_springbootweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author 黑炎
 * @since 2026-07-05
 */
@Getter
@Setter
@TableName("t_bookshelf")
public class Bookshelf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 书架ID
     */
    @TableId(value = "bookshelfid", type = IdType.AUTO)
    private Integer bookshelfid;

    /**
     * 用户ID
     */
    @TableField("userid")
    private Integer userid;

    /**
     * 漫画ID
     */
    @TableField("comicid")
    private Integer comicid;

    /**
     * 收藏时间
     */
    @TableField("collecttime")
    private LocalDateTime collecttime;
}
