package com.example.comicplatform_springbootweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.comicplatform_springbootweb.entity.Comic;

import java.util.List;

public interface ComicService {
    Comic addComic(Comic comic);//增加漫画
    Comic updateComic(Comic comic);//修改漫画
    void deleteComic(Integer id);//删除漫画
    Comic getComicById(Integer id);//根据 ID 查询漫画详情
    List<Comic> searchComics(String keyword);//根据名称、作者、关键字模糊查询
    List<Comic> getComicsByCategory(String category);//根据类别查询漫画

    //分页查询漫画（翻页用）
    Page<Comic> listComicsWithPage(Integer pageNum, Integer pageSize, String keyword, String category);

    //根据作者查询漫画
    List<Comic> getComicsByAuthor(String author);
}