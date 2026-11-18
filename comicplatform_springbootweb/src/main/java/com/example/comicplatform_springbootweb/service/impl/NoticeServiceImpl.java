package com.example.comicplatform_springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.comicplatform_springbootweb.entity.Notice;
import com.example.comicplatform_springbootweb.mapper.NoticeMapper;
import com.example.comicplatform_springbootweb.service.NoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告业务逻辑实现类
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    //新增公告，自动补全发布时间与默认状态
    public Notice addNotice(Notice notice) {
        notice.setPublishdate(LocalDateTime.now());
        if (notice.getStatus() == null || notice.getStatus().isEmpty()) {
            notice.setStatus("已发布");
        }
        noticeMapper.insert(notice);
        return notice;
    }

    @Override
    //修改公告
    public Notice updateNotice(Notice notice) {
        noticeMapper.updateById(notice);
        return notice;
    }

    @Override
    //删除公告
    public void deleteNotice(Integer id) {
        noticeMapper.deleteById(id);
    }

    @Override
    //根据ID查询公告
    public Notice getNoticeById(Integer id) {
        return noticeMapper.selectById(id);
    }

    @Override
    //查询全部公告，按发布时间倒序，最新的排前面
    public List<Notice> selectAll() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notice::getPublishdate)
               .orderByDesc(Notice::getNoticeid);
        return noticeMapper.selectList(wrapper);
    }

    @Override
    //只查询已发布的公告，供首页公告栏展示
    public List<Notice> getPublishedNotices() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, "已发布")
               .orderByDesc(Notice::getPublishdate)
               .orderByDesc(Notice::getNoticeid);
        return noticeMapper.selectList(wrapper);
    }
}
