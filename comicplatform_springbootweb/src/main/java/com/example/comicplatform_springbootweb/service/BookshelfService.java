package com.example.comicplatform_springbootweb.service;

import com.example.comicplatform_springbootweb.entity.Bookshelf;
import com.example.comicplatform_springbootweb.vo.BookshelfVO;

import java.util.List;
/**
 * 书架服务接口
 * 提供书架相关的业务逻辑
 * @author 黑炎
 * @since 2026-07-05
 *
 * 二次修改：
 * 无法接收后端是否收藏漫画，同时还有加入和取消收藏按钮
 *## 3.2 企业标准的 REST 接口设计
 * 书架接口应该这样设计：
 *
 * 功能 方法 路径 参数
 * 检查是否收藏 GET /api/bookshelf/check userId , comicId
 * 加入书架 POST /api/bookshelf Body: { userId, comicId }
 * 取消收藏 DELETE /api/bookshelf userId , comicId
 * 获取用户书架 GET /api/bookshelf/user/{userId} 路径参数
 *
 *
 */

public interface BookshelfService {
    Bookshelf addToBookshelf(Integer userId, Integer comicId);
    void removeFromBookshelf(Integer bookshelfId);
    /**
     * 取消收藏
     * 从书架中移除指定用户的收藏漫画
     * 二次修改位置按用户ID和漫画ID取消收藏，方便前端操作
     * 时间：2026-07-06
     */

    void removeFromBookshelf(Integer userId, Integer comicId);
    List<Bookshelf> getUserBookshelf(Integer userId);

    // 返回带漫画完整信息的书架列表，给前端书架页使用
    List<BookshelfVO> getUserBookshelfWithComicInfo(Integer userId);

    boolean isCollected(Integer userId, Integer comicId);
}
