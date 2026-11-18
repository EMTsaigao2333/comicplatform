package com.example.comicplatform_springbootweb.service;

import com.example.comicplatform_springbootweb.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  章节服务接口
 * </p>
 *
 * @author 黑炎
 * @since 2026-07-05
 */

public interface ChapterService {
    Chapter addChapter(Chapter chapter);//增加章节
    Chapter updateChapter(Chapter chapter);//修改章节信息
    void deleteChapter(Integer id);//删除章节
    Chapter getChapterById(Integer id);//根据ID查询章节详情
    List<Chapter> getChaptersByComicId(Integer comicId);//根据漫画ID查询章节章节列表
    void updateStatus(Integer id, String status);//更新章节发布状态
}
