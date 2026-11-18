package com.example.comicplatform_springbootweb.service;

import com.example.comicplatform_springbootweb.entity.Category;

import java.util.List;

/**
 * 分类业务逻辑接口
 */
public interface CategoryService {

    //新增分类
    Category addCategory(Category category);

    //修改分类
    Category updateCategory(Category category);

    //删除分类
    void deleteCategory(Integer id);

    //根据ID查询分类
    Category getCategoryById(Integer id);

    //查询全部分类（按排序号升序）
    List<Category> selectAll();

    //查询启用状态的分类
    List<Category> getEnabledCategories();
}
