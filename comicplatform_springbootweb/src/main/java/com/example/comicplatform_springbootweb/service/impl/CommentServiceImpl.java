package com.example.comicplatform_springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.comicplatform_springbootweb.entity.Comment;
import com.example.comicplatform_springbootweb.mapper.CommentMapper;
import com.example.comicplatform_springbootweb.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论业务逻辑实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    //新增评论，自动补全评论时间与默认状态
    public Comment addComment(Comment comment) {
        comment.setCreatetime(LocalDateTime.now());
        if (comment.getStatus() == null || comment.getStatus().isEmpty()) {
            comment.setStatus("正常");
        }
        if (comment.getRating() == null) {
            comment.setRating(5);
        }
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    //修改评论
    public Comment updateComment(Comment comment) {
        commentMapper.updateById(comment);
        return comment;
    }

    @Override
    //删除评论
    public void deleteComment(Integer id) {
        commentMapper.deleteById(id);
    }

    @Override
    //根据ID查询评论
    public Comment getCommentById(Integer id) {
        return commentMapper.selectById(id);
    }

    @Override
    //查询全部评论（管理员后台使用），按时间倒序
    public List<Comment> selectAll() {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Comment::getCreatetime)
               .orderByDesc(Comment::getCommentid);
        return commentMapper.selectList(wrapper);
    }

    @Override
    //查询某本漫画下状态为正常的评论，供详情页展示
    public List<Comment> getCommentsByComic(Integer comicId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getComicid, comicId)
               .eq(Comment::getStatus, "正常")
               .orderByDesc(Comment::getCreatetime);
        return commentMapper.selectList(wrapper);
    }

    @Override
    //屏蔽或恢复评论
    public Comment updateStatus(Integer id, String status) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setStatus(status);
            commentMapper.updateById(comment);
        }
        return comment;
    }

    @Override
    //统计某本漫画的平均评分，保留一位小数
    public Double getAverageRating(Integer comicId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getComicid, comicId)
               .eq(Comment::getStatus, "正常");
        List<Comment> list = commentMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Comment c : list) {
            sum += c.getRating() == null ? 0 : c.getRating();
        }
        return Math.round(sum / list.size() * 10) / 10.0;
    }
}
