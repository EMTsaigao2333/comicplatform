package com.example.comicplatform_springbootweb.controller;

import com.example.comicplatform_springbootweb.common.Result;
import com.example.comicplatform_springbootweb.entity.Category;
import com.example.comicplatform_springbootweb.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类管理控制器
 * 提供分类的增删改查接口，供管理员后台与作者发布页使用
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    //新增分类
    @PostMapping
    public Result<Category> add(@RequestBody Category category) {
        return Result.success(categoryService.addCategory(category));
    }

    //修改分类，根据路径中的 id 更新
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable("id") Integer id, @RequestBody Category category) {
        category.setCategoryid(id);
        return Result.success(categoryService.updateCategory(category));
    }

    //删除分类
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Integer id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    //根据ID查询分类
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable("id") Integer id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    //查询全部分类（管理员后台使用）
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.selectAll());
    }

    //查询启用状态的分类（作者发布漫画时使用）
    @GetMapping("/enabled")
    public Result<List<Category>> listEnabled() {
        return Result.success(categoryService.getEnabledCategories());
    }
}
