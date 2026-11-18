package com.example.comicplatform_springbootweb.controller;

import com.example.comicplatform_springbootweb.common.Result;
import com.example.comicplatform_springbootweb.entity.Notice;
import com.example.comicplatform_springbootweb.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告管理控制器
 * 提供公告的增删改查接口，供首页公告栏与管理员后台使用
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    //新增公告
    @PostMapping
    public Result<Notice> add(@RequestBody Notice notice) {
        return Result.success(noticeService.addNotice(notice));
    }

    //修改公告，根据路径中的 id 更新
    @PutMapping("/{id}")
    public Result<Notice> update(@PathVariable("id") Integer id, @RequestBody Notice notice) {
        notice.setNoticeid(id);
        return Result.success(noticeService.updateNotice(notice));
    }

    //删除公告
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Integer id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }

    //根据ID查询公告
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable("id") Integer id) {
        return Result.success(noticeService.getNoticeById(id));
    }

    //查询全部公告（管理员后台使用）
    @GetMapping
    public Result<List<Notice>> list() {
        return Result.success(noticeService.selectAll());
    }

    //查询已发布公告（首页公告栏使用）
    @GetMapping("/published")
    public Result<List<Notice>> listPublished() {
        return Result.success(noticeService.getPublishedNotices());
    }
}
