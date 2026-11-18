package com.example.comicplatform_springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.comicplatform_springbootweb.entity.Category;
import com.example.comicplatform_springbootweb.mapper.CategoryMapper;
import com.example.comicplatform_springbootweb.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类业务逻辑实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    //新增分类，自动补全创建时间与默认状态
    public Category addCategory(Category category) {
        category.setCreatetime(LocalDateTime.now());
        if (category.getStatus() == null || category.getStatus().isEmpty()) {
            category.setStatus("启用");
        }
        categoryMapper.insert(category);
        return category;
    }

    @Override
    //修改分类
    public Category updateCategory(Category category) {
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    //删除分类
    public void deleteCategory(Integer id) {
        categoryMapper.deleteById(id);
    }

    @Override
    //根据ID查询分类
    public Category getCategoryById(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    //查询全部分类，按排序号升序排列
    public List<Category> selectAll() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortorder);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    //只查询启用状态的分类，供作者发布漫画时选择
    public List<Category> getEnabledCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, "启用")
               .orderByAsc(Category::getSortorder);
        return categoryMapper.selectList(wrapper);
    }
}
