package com.example.comicplatform_springbootweb.service;

import com.example.comicplatform_springbootweb.entity.Notice;

import java.util.List;

/**
 * 公告业务逻辑接口
 */
public interface NoticeService {

    //新增公告
    Notice addNotice(Notice notice);

    //修改公告
    Notice updateNotice(Notice notice);

    //删除公告
    void deleteNotice(Integer id);

    //根据ID查询公告
    Notice getNoticeById(Integer id);

    //查询全部公告（按发布时间倒序）
    List<Notice> selectAll();

    //查询已发布公告（首页公告栏使用）
    List<Notice> getPublishedNotices();
}
