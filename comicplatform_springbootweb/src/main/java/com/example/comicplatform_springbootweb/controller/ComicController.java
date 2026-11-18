package com.example.comicplatform_springbootweb.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.comicplatform_springbootweb.common.Result;
import com.example.comicplatform_springbootweb.entity.Comic;
import com.example.comicplatform_springbootweb.service.ComicService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/comic")
public class ComicController {

    @Resource
    private ComicService comicService;
    //增加漫画
    @PostMapping
    public Result<Comic> add(@RequestBody Comic comic) {
        return Result.success(comicService.addComic(comic));
    }

    // 修改漫画，根据路径中的 id 更新
    @PutMapping("/{id}")
    public Result<Comic> update(@PathVariable Integer id, @RequestBody Comic comic) {
        comic.setComicId(id);
        return Result.success(comicService.updateComic(comic));
    }
    //删除漫画
    @DeleteMapping("/{id}")//配合@PathVariable使用把动态参数id绑定到方法参数id
    public Result<Void> delete(@PathVariable Integer id) {
        comicService.deleteComic(id);
        return Result.success();
    }

    //根据 ID 查询漫画详情
    @GetMapping("/{id}")
    public Result<Comic> getById(@PathVariable Integer id) {
        return Result.success(comicService.getComicById(id));
    }

    //根据名称、作者、关键字模糊查询
    @GetMapping("/search")
    public Result<java.util.List<Comic>> search(@RequestParam String keyword) {
        return Result.success(comicService.searchComics(keyword));
    }

    //根据类别查询漫画
    @GetMapping("/category/{category}")
    public Result<java.util.List<Comic>> getByCategory(@PathVariable String category) {
        return Result.success(comicService.getComicsByCategory(category));
    }

    //根据作者查询漫画
    @GetMapping("/author")
    public Result<java.util.List<Comic>> getByAuthor(@RequestParam String author) {
        return Result.success(comicService.getComicsByAuthor(author));
    }

    //分页查询漫画（翻页用）
    @GetMapping("/page")
    public Result<Page<Comic>> listPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.success(comicService.listComicsWithPage(pageNum, pageSize, keyword, category));
    }

    // 标记/取消违规
    @PutMapping("/{id}/violation")
    public Result<Void> toggleViolation(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Integer isViolation = (Integer) body.get("isViolation");
        Comic comic = comicService.getComicById(id);
        if (comic != null) {
            comic.setIsviolation(isViolation);
            comicService.updateComic(comic);
        }
        return Result.success();
    }

    // 下架/上架
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        Comic comic = comicService.getComicById(id);
        if (comic != null) {
            comic.setStatus(status);
            comicService.updateComic(comic);
        }
        return Result.success();
    }
}