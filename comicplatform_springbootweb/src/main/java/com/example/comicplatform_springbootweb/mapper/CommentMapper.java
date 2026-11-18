package com.example.comicplatform_springbootweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.comicplatform_springbootweb.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论数据访问接口
 * 继承 BaseMapper 后自带 insert、deleteById、updateById、selectById、selectList 等通用方法
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
