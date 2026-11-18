package com.example.comicplatform_springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.comicplatform_springbootweb.entity.Comic;
import com.example.comicplatform_springbootweb.mapper.ComicMapper;
import com.example.comicplatform_springbootweb.service.ComicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service//注入容器
public class ComicServiceImpl implements ComicService {//重写接口操作方式


     // @Resource自动注入 MyBatis-Plus
    //生成的 Mapper 对象，所有数据库增删改查都通过comicMapper完成。
    @Resource
    private ComicMapper comicMapper;

    @Override
    public Comic addComic(Comic comic) {
        comic.setCreatetime(LocalDateTime.now());
        comicMapper.insert(comic);
        return comic;
    }


    @Override
    public Comic updateComic(Comic comic) {//修改漫画
        comicMapper.updateById(comic);
        return comic;
    }

    @Override
    public void deleteComic(Integer id) {//删除漫画
        comicMapper.deleteById(id);
    }

    @Override
    public Comic getComicById(Integer id) {//根据ID查询哪一本漫画
        return comicMapper.selectById(id);
    }


    // ❌ 传统方式：字段名写错编译不报错，运行时才炸
    //wrapper.eq("comictitle", "龙珠");
    // ✅ Lambda 方式：写错 IDE 直接标红
    //wrapper.eq(Comic::getComicTitle, "龙珠");
    @Override//根据漫画标题、作者、关键词模糊查询漫画
    public List<Comic> searchComics(String keyword) {//根据漫画标题、作者、关键词模糊查询漫画
        //Lambda 条件构造器，不用写字符串字段名，通过实体方法引用Comic::getComicTitle，解释在上面
        LambdaQueryWrapper<Comic> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Comic::getComicTitle, keyword)//模糊查询WHERE comictitle LIKE '%keyword%'
                .or()//链接多个条件
                .like(Comic::getAuthor, keyword)
                .or()
                .like(Comic::getKeywords, keyword);
        return comicMapper.selectList(wrapper);//返回集合
    }

    //根据漫画分类查询
    @Override
    public List<Comic> getComicsByCategory(String category) {
        LambdaQueryWrapper<Comic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comic::getCategory, category);
        return comicMapper.selectList(wrapper);
    }

    //多条件查询漫画分页
    @Override
    public Page<Comic> listComicsWithPage(Integer pageNum, Integer pageSize, String keyword, String category) {
        //创建分页对象：当前页码、每页数量
        Page<Comic> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comic> wrapper = new LambdaQueryWrapper<>();
        //关键词不为空，增加搜索条件
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Comic::getComicTitle, keyword)
                    .or()
                    .like(Comic::getAuthor, keyword)
                    .or()
                    .like(Comic::getKeywords, keyword));
        }
        //分类不为空，增加分类筛选条件
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Comic::getCategory, category);
        }
        // 过滤已下架漫画
        wrapper.ne(Comic::getStatus, "已下架");
        //根据id倒叙，最新的排在前面
        wrapper.orderByDesc(Comic::getComicId);
        //返回带你分页信息的对象
        return comicMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Comic> getComicsByAuthor(String author) {
        LambdaQueryWrapper<Comic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comic::getAuthor, author)
               .orderByDesc(Comic::getComicId);
        return comicMapper.selectList(wrapper);
    }
}