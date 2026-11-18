package com.example.comicplatform_springbootweb.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书架视图对象
 * 为什么需要这个vo这数据对象，漫画封面、漫画标题、作者、分类、最新章节、收藏时间
 *这些信息分散在 t_bookshelf 、 t_comic 、 t_chapter 多张表里。
 * 如果直接把 Bookshelf 实体返回给前端，前端拿到的是：
 *{ "bookshelfid": 1, "userid": 1, "comicid": 1, "collecttime": "..." }
 * 需要多表组成一个新对象，才能在前端展示完整的信息
 * 为什么不用v_bookshelf：查询逻辑复杂、需要业务判断、要分页，方便后面加缓存、加阅读进度等业务逻辑
 */
@Data
public class BookshelfVO {

    private Integer bookshelfid;
    private Integer userid;
    private Integer comicid;

    // 漫画信息
    private String comictitle;
    private String author;
    private String coverimg;
    private String category;
    private String intro;

    // 收藏时间
    private LocalDateTime collecttime;

    // 最新章节信息
    private LocalDateTime lastUpdateTime;
    private Integer chapterCount;
}
