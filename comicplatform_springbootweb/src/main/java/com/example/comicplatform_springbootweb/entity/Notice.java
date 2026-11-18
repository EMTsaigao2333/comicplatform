package com.example.comicplatform_springbootweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台公告实体类
 * 用于维护首页公告栏内容（公告、活动、更新、招募）
 */
@TableName("t_notice")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公告ID（主键，数据库自增）
     */
    @TableId(value = "noticeid", type = IdType.AUTO)
    private Integer noticeid;

    /**
     * 公告标题
     */
    @TableField("title")
    private String title;

    /**
     * 公告正文
     */
    @TableField("content")
    private String content;

    /**
     * 公告类型：公告/活动/更新/招募
     */
    @TableField("noticetype")
    private String noticetype;

    /**
     * 发布人
     */
    @TableField("publisher")
    private String publisher;

    /**
     * 发布时间
     */
    @TableField("publishdate")
    private LocalDateTime publishdate;

    /**
     * 公告状态：已发布/已下线
     */
    @TableField("status")
    private String status;

    public Integer getNoticeid() {
        return noticeid;
    }

    public void setNoticeid(Integer noticeid) {
        this.noticeid = noticeid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNoticetype() {
        return noticetype;
    }

    public void setNoticetype(String noticetype) {
        this.noticetype = noticetype;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDateTime getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(LocalDateTime publishdate) {
        this.publishdate = publishdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
