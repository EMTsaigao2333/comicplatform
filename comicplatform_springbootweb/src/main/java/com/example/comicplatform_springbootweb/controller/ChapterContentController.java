package com.example.comicplatform_springbootweb.controller;

import com.example.comicplatform_springbootweb.common.Result;
import com.example.comicplatform_springbootweb.entity.ChapterContent;
import com.example.comicplatform_springbootweb.service.ChapterContentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/chapter-content")
public class ChapterContentController {

    @Resource
    private ChapterContentService chapterContentService;

    @PostMapping
    public Result<ChapterContent> add(@RequestBody ChapterContent content) {
        return Result.success(chapterContentService.addContent(content));
    }

    @PutMapping
    public Result<ChapterContent> update(@RequestBody ChapterContent content) {
        return Result.success(chapterContentService.updateContent(content));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        chapterContentService.deleteContent(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ChapterContent> getById(@PathVariable Integer id) {
        return Result.success(chapterContentService.getContentById(id));
    }

    @GetMapping("/chapter/{chapterId}")
    public Result<List<ChapterContent>> getByChapterId(@PathVariable Integer chapterId) {
        return Result.success(chapterContentService.getContentsByChapterId(chapterId));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        chapterContentService.updateContentStatus(id, status);
        return Result.success();
    }
}
