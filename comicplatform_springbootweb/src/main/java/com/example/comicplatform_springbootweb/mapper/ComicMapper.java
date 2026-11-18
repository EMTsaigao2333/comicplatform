package com.example.comicplatform_springbootweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.comicplatform_springbootweb.entity.Comic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 这是 MyBatis-Plus 最爽的地方 ： BaseMapper<Comic> 自带以下方法，一行都不用写：
 *
 * - insert(comic) — 新增
 * - deleteById(id) — 按 ID 删除
 * - updateById(comic) — 按 ID 更新
 * - selectById(id) — 按 ID 查询
 * - selectList(wrapper) — 条件查询
 * - selectPage(page, wrapper) — 分页查询
 * 就这一个空接口，增删改查全有了。你需求文档里 ComicDAO 要写的那些方法，BaseMapper 全包了。
 */

@Mapper
public interface ComicMapper extends BaseMapper<Comic> {
}
