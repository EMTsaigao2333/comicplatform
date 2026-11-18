package com.example.comicplatform_springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.comicplatform_springbootweb.entity.Bookshelf;
import com.example.comicplatform_springbootweb.entity.Chapter;
import com.example.comicplatform_springbootweb.entity.Comic;
import com.example.comicplatform_springbootweb.mapper.BookshelfMapper;
import com.example.comicplatform_springbootweb.mapper.ChapterMapper;
import com.example.comicplatform_springbootweb.mapper.ComicMapper;
import com.example.comicplatform_springbootweb.service.BookshelfService;
import com.example.comicplatform_springbootweb.vo.BookshelfVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 书架服务实现类
 * 提供书架相关的业务逻辑实现
 * @author 黑炎
 * @since 2026-07-05
 *
 * 二次修改：
 * 实现无法接收后端是否收藏漫画，同时还有加入和取消收藏按钮
 */
@Service
public class BookshelfServiceImpl implements BookshelfService {

    @Resource
    private BookshelfMapper bookshelfMapper;

    @Resource
    private ComicMapper comicMapper;

    @Resource
    private ChapterMapper chapterMapper;


    @Override
    //添加到书架
    public Bookshelf addToBookshelf(Integer userId, Integer comicId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserid, userId)
               .eq(Bookshelf::getComicid, comicId);
        //selectCount 比 selectOne 高效——只发 SELECT COUNT(*) ，不查具体数据。
        //这里只想知道"收藏了没"，不需要完整记录，用 selectCount 最合适。只查数量，不查数据
        if (bookshelfMapper.selectCount(wrapper) > 0) {// 如果收藏了，抛出异常
            throw new RuntimeException("该漫画已在书架中");
        }
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setUserid(userId);
        bookshelf.setComicid(comicId);
        bookshelf.setCollecttime(LocalDateTime.now());
        bookshelfMapper.insert(bookshelf);
        return bookshelf;
    }

    @Override
    //从书架移除漫画
    public void removeFromBookshelf(Integer bookshelfId) {
        bookshelfMapper.deleteById(bookshelfId);
    }

    /**
     *二次修改位置：
     * 时间：2026-07-06
     */
    @Override
    // 按用户ID和漫画ID移除收藏
    public void removeFromBookshelf(Integer userId, Integer comicId) {
        // 创建MyBatis-Plus条件构造器，用于拼接删除条件
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        // 拼接查询条件：用户id等于传入userId 并且 漫画id等于传入comicId
        wrapper.eq(Bookshelf::getUserid, userId)
                .eq(Bookshelf::getComicid, comicId);
        // 根据上面拼接好的条件，删除书架表对应的收藏记录
        bookshelfMapper.delete(wrapper);
    }

    @Override
    //根据用户ID查询书架
    public List<Bookshelf> getUserBookshelf(Integer userId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserid, userId)
               .orderByDesc(Bookshelf::getCollecttime);
        return bookshelfMapper.selectList(wrapper);
    }

    @Override
    // 查询用户书架，并附带漫画信息和最新章节统计
    public List<BookshelfVO> getUserBookshelfWithComicInfo(Integer userId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserid, userId)
               .orderByDesc(Bookshelf::getCollecttime);
        List<Bookshelf> list = bookshelfMapper.selectList(wrapper);

        List<BookshelfVO> result = new ArrayList<>();
        for (Bookshelf b : list) {
            BookshelfVO vo = new BookshelfVO();
            vo.setBookshelfid(b.getBookshelfid());
            vo.setUserid(b.getUserid());
            vo.setComicid(b.getComicid());
            vo.setCollecttime(b.getCollecttime());

            // 查询漫画信息
            Comic comic = comicMapper.selectById(b.getComicid());
            if (comic != null) {
                vo.setComictitle(comic.getComicTitle());
                vo.setAuthor(comic.getAuthor());
                vo.setCoverimg(comic.getCoverImg());
                vo.setCategory(comic.getCategory());
                vo.setIntro(comic.getIntro());
            }

            // 查询该漫画已发布章节，按发布时间倒序
            LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
            chapterWrapper.eq(Chapter::getComicid, b.getComicid())
                          .eq(Chapter::getStatus, "发布")
                          .orderByDesc(Chapter::getReleasedate);
            List<Chapter> chapters = chapterMapper.selectList(chapterWrapper);
            vo.setChapterCount(chapters.size());
            if (!chapters.isEmpty()) {
                vo.setLastUpdateTime(chapters.get(0).getReleasedate());
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    //判断用户是否收藏了漫画
    public boolean isCollected(Integer userId, Integer comicId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserid, userId)
               .eq(Bookshelf::getComicid, comicId);
        return bookshelfMapper.selectCount(wrapper) > 0;
    }
}
