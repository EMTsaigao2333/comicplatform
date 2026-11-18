package com.example.comicplatform_springbootweb.service;

import com.example.comicplatform_springbootweb.entity.Comment;

import java.util.List;

/**
 * 评论业务逻辑接口
 */
public interface CommentService {

    //新增评论
    Comment addComment(Comment comment);

    //修改评论
    Comment updateComment(Comment comment);

    //删除评论
    void deleteComment(Integer id);

    //根据ID查询评论
    Comment getCommentById(Integer id);

    //查询全部评论（管理员后台使用，按时间倒序）
    List<Comment> selectAll();

    //查询某本漫画的正常评论（详情页使用）
    List<Comment> getCommentsByComic(Integer comicId);

    //屏蔽/恢复评论
    Comment updateStatus(Integer id, String status);

    //统计某本漫画的平均评分
    Double getAverageRating(Integer comicId);
}
