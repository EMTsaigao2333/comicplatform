package com.example.comicplatform_springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.comicplatform_springbootweb.entity.ChapterContent;
import com.example.comicplatform_springbootweb.mapper.ChapterContentMapper;
import com.example.comicplatform_springbootweb.service.ChapterContentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChapterContentServiceImpl implements ChapterContentService {

    @Resource
    private ChapterContentMapper chapterContentMapper;

    @Override
    public ChapterContent addContent(ChapterContent content) {
        chapterContentMapper.insert(content);
        return content;
    }

    @Override
    public ChapterContent updateContent(ChapterContent content) {
        chapterContentMapper.updateById(content);
        return content;
    }

    @Override
    public void deleteContent(Integer id) {
        chapterContentMapper.deleteById(id);
    }

    @Override
    public ChapterContent getContentById(Integer id) {
        return chapterContentMapper.selectById(id);
    }

    @Override
    public List<ChapterContent> getContentsByChapterId(Integer chapterId) {
        LambdaQueryWrapper<ChapterContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterContent::getChapterid, chapterId)
               .orderByAsc(ChapterContent::getDisplayorder);
        return chapterContentMapper.selectList(wrapper);
    }

    @Override
    public void updateContentStatus(Integer id, String status) {
        LambdaUpdateWrapper<ChapterContent> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChapterContent::getContentid, id)
               .set(ChapterContent::getStatus, status);
        chapterContentMapper.update(null, wrapper);
    }
}
