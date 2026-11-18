package com.example.comicplatform_springbootweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 漫画评论实体类
 * 读者可在漫画详情页发表评论并打分，管理员可对违规评论进行屏蔽
 */
@TableName("t_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID（主键，数据库自增）
     */
    @TableId(value = "commentid", type = IdType.AUTO)
    private Integer commentid;

    /**
     * 漫画ID，关联 t_comic
     */
    @TableField("comicid")
    private Integer comicid;

    /**
     * 评论用户ID，关联 t_user
     */
    @TableField("userid")
    private Integer userid;

    /**
     * 评论用户名（冗余存储，方便列表展示）
     */
    @TableField("username")
    private String username;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 评分：1-5 分
     */
    @TableField("rating")
    private Integer rating;

    /**
     * 评论时间
     */
    @TableField("createtime")
    private LocalDateTime createtime;

    /**
     * 评论状态：正常/已屏蔽
     */
    @TableField("status")
    private String status;

    public Integer getCommentid() {
        return commentid;
    }

    public void setCommentid(Integer commentid) {
        this.commentid = commentid;
    }

    public Integer getComicid() {
        return comicid;
    }

    public void setComicid(Integer comicid) {
        this.comicid = comicid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
