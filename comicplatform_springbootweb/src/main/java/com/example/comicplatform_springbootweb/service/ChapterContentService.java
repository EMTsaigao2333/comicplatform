package com.example.comicplatform_springbootweb.service;

import com.example.comicplatform_springbootweb.entity.ChapterContent;

import java.util.List;

public interface ChapterContentService {
    // 1. 新增单张章节图片内容
    ChapterContent addContent(ChapterContent content);

    // 2. 修改章节图片信息
    ChapterContent updateContent(ChapterContent content);

    // 3. 根据内容ID删除一张章节图片
    void deleteContent(Integer id);

    // 4. 根据图片内容ID查询单条图片详情
    ChapterContent getContentById(Integer id);

    // 5. 根据章节ID，查询该章节下所有图片分页/列表
    List<ChapterContent> getContentsByChapterId(Integer chapterId);

    // 6. 单独修改图片的上下架状态
    void updateContentStatus(Integer id, String status);
}
