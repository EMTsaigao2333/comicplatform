package com.example.comicplatform_springbootweb.controller;

import com.example.comicplatform_springbootweb.common.Result;
import com.example.comicplatform_springbootweb.entity.Comment;
import com.example.comicplatform_springbootweb.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 评论管理控制器
 * 提供评论的增删改查接口，供漫画详情页与管理员后台使用
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    //新增评论
    @PostMapping
    public Result<Comment> add(@RequestBody Comment comment) {
        return Result.success(commentService.addComment(comment));
    }

    //修改评论，根据路径中的 id 更新
    @PutMapping("/{id}")
    public Result<Comment> update(@PathVariable("id") Integer id, @RequestBody Comment comment) {
        comment.setCommentid(id);
        return Result.success(commentService.updateComment(comment));
    }

    //删除评论
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Integer id) {
        commentService.deleteComment(id);
        return Result.success();
    }

    //根据ID查询评论
    @GetMapping("/{id}")
    public Result<Comment> getById(@PathVariable("id") Integer id) {
        return Result.success(commentService.getCommentById(id));
    }

    //查询全部评论（管理员后台使用）
    @GetMapping
    public Result<List<Comment>> list() {
        return Result.success(commentService.selectAll());
    }

    //查询某本漫画的正常评论（详情页使用）
    @GetMapping("/comic/{comicId}")
    public Result<List<Comment>> listByComic(@PathVariable("comicId") Integer comicId) {
        return Result.success(commentService.getCommentsByComic(comicId));
    }

    //屏蔽/恢复评论
    @PutMapping("/{id}/status")
    public Result<Comment> updateStatus(@PathVariable("id") Integer id, @RequestBody Map<String, String> body) {
        return Result.success(commentService.updateStatus(id, body.get("status")));
    }

    //查询某本漫画的平均评分
    @GetMapping("/rating/{comicId}")
    public Result<Double> getAverageRating(@PathVariable("comicId") Integer comicId) {
        return Result.success(commentService.getAverageRating(comicId));
    }
}
