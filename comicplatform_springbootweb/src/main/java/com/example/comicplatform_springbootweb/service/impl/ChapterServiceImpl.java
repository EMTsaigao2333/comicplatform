package com.example.comicplatform_springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.comicplatform_springbootweb.entity.Chapter;
import com.example.comicplatform_springbootweb.mapper.ChapterMapper;
import com.example.comicplatform_springbootweb.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  章节服务实现类
 * </p>
 *
 * @author 黑炎
 * @since 2026-07-05
 */
@Service
public class ChapterServiceImpl implements ChapterService {

    @Resource
    private ChapterMapper chapterMapper;

    @Override
    public Chapter addChapter(Chapter chapter) {
        if (chapter.getReleasedate() == null) {
            chapter.setReleasedate(LocalDateTime.now());
        }
        chapterMapper.insert(chapter);
        return chapter;
    }

    @Override
    public Chapter updateChapter(Chapter chapter) {
        chapterMapper.updateById(chapter);
        return chapter;
    }

    @Override
    public void deleteChapter(Integer id) {
        chapterMapper.deleteById(id);
    }

    @Override
    public Chapter getChapterById(Integer id) {
        return chapterMapper.selectById(id);
    }

    @Override
    public List<Chapter> getChaptersByComicId(Integer comicId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getComicid, comicId)
                .orderByAsc(Chapter::getChapterno);
        return chapterMapper.selectList(wrapper);
    }

    @Override//更新章节状态（如：已发布、已删除等）
    public void updateStatus(Integer id, String status) {
        //updateById先查再改再存（改多个字段），这个是改只一个字段
        //这里只改发布状态，用 LambdaUpdateWrapper 更高效，不需要先查数据库。
        LambdaUpdateWrapper<Chapter> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Chapter::getChapterid, id)
                .set(Chapter::getStatus, status);
        chapterMapper.update(null, wrapper);
    }
}
