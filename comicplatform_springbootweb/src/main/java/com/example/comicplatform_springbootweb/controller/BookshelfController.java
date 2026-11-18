package com.example.comicplatform_springbootweb.controller;

import com.example.comicplatform_springbootweb.common.Result;
import com.example.comicplatform_springbootweb.entity.Bookshelf;
import com.example.comicplatform_springbootweb.service.BookshelfService;
import com.example.comicplatform_springbootweb.vo.BookshelfVO;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/bookshelf")
public class BookshelfController {

    @Resource
    private BookshelfService bookshelfService;


    /**
     * 加入书架
     * 企业标准：新增用 @RequestBody 接收 JSON，更符合 REST 风格
     * 二次修改位置：
     * 修改时间：2026-07-06
     */
    @PostMapping
//    public Result<Bookshelf> add(@RequestParam Integer userId, @RequestParam Integer comicId) {
//        return Result.success(bookshelfService.addToBookshelf(userId, comicId));
//    }
    public Result<Bookshelf> add(@RequestBody Bookshelf bookshelf) {
        return Result.success(bookshelfService.addToBookshelf(bookshelf.getUserid(), bookshelf.getComicid()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        bookshelfService.removeFromBookshelf(id);
        return Result.success();
    }

    /**
     * 取消收藏
     * 前端常用：按 userId + comicId 取消收藏
     * 二次修改位置：
     * 修改时间：2026-07-06
     */
    @DeleteMapping
    public Result<Void> deleteByUserAndComic(@RequestParam Integer userId, @RequestParam Integer comicId) {
        bookshelfService.removeFromBookshelf(userId, comicId);
        return Result.success();
    }

    @GetMapping("/user/{userId}")
    // public Result<List<Bookshelf>> getUserBookshelf(@PathVariable Integer userId) {
    //     return Result.success(bookshelfService.getUserBookshelf(userId));
    // }
     public Result<List<BookshelfVO>> getUserBookshelf(@PathVariable Integer userId) {
        return Result.success(bookshelfService.getUserBookshelfWithComicInfo(userId));
    }

    @GetMapping("/check")
    public Result<Boolean> checkCollected(@RequestParam Integer userId, @RequestParam Integer comicId) {
        return Result.success(bookshelfService.isCollected(userId, comicId));
    }
}
