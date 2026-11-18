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
@TableName("t_chapter")
public class Chapter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 章节ID
     */
    @TableId(value = "chapterid", type = IdType.AUTO)
    private Integer chapterid;

    /**
     * 漫画ID
     */
    @TableField("comicid")
    private Integer comicid;

    /**
     * 章节编号
     */
    @TableField("chapterno")
    private Integer chapterno;

    /**
     * 章节标题
     */
    @TableField("chaptertitle")
    private String chaptertitle;

    /**
     * 发布日期
     */
    @TableField("releasedate")
    private LocalDateTime releasedate;

    /**
     * 发布状态
     */
    @TableField("status")
    private String status;

    /**
     * 章节内容图片URL数组，以JSON字符串存储
     */
    @TableField("content")
    private String content;
}
