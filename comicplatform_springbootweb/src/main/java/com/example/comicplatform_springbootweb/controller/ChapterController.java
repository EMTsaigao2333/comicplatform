package com.example.comicplatform_springbootweb.controller;

import com.example.comicplatform_springbootweb.common.Result;
import com.example.comicplatform_springbootweb.entity.Chapter;
import com.example.comicplatform_springbootweb.service.ChapterService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黑炎
 * @since 2026-07-05
 * /comic/list 一个 HTML 页面（给浏览器看的）
 * /api/comic/page 一段 JSON 数据（给前端 Vue/React 调用的）
 *
 * 前后端分离跨域要用
 * 有了 /api 统一前缀，Nginx 就能精确判断：
 * /api/* → 转发到后端 Java
 *其他所有 → 返回前端静态页面
 * 如果你不加 /api ，以后部署时每加一个 Controller 都要改 Nginx 配置，加了就一劳永逸。
 */
@RestController
//为何加api前缀，避免接口和前端页面冲突
@RequestMapping("/api/chapter")
public class ChapterController {


    @Resource
    private ChapterService chapterService;

    //增加章节
    @PostMapping
    public Result<Chapter> add(@RequestBody Chapter chapter) {
        return Result.success(chapterService.addChapter(chapter));
    }

//    //修改章节
//    @PutMapping
//    public Result<Chapter> update(@RequestBody Chapter chapter) {
//        return Result.success(chapterService.updateChapter(chapter));
//    }
    // 修改章节，根据路径中的 id 更新
    @PutMapping("/{id}")
    public Result<Chapter> update(@PathVariable Integer id, @RequestBody Chapter chapter) {
        chapter.setChapterid(id);
        return Result.success(chapterService.updateChapter(chapter));
    }

    //删除章节
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        chapterService.deleteChapter(id);
        return Result.success();
    }

    //根据 ID 查询章节详情
    @GetMapping("/{id}")
    public Result<Chapter> getById(@PathVariable Integer id) {
        return Result.success(chapterService.getChapterById(id));
    }

    //根据漫画 ID 查询章节列表
    @GetMapping("/comic/{comicId}")
    public Result<List<Chapter>> getByComicId(@PathVariable Integer comicId) {
        return Result.success(chapterService.getChaptersByComicId(comicId));
    }

    //更新章节状态（如：已发布、已删除等）
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        chapterService.updateStatus(id, status);
        return Result.success();
    }
}