package com.example.comicplatform_springbootweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author 黑炎
 * @since 2026-07-05
 */
@Getter
@Setter
@ToString//替代三个方法
@TableName("t_chaptercontent")
public class ChapterContent implements Serializable {

    /**
     * 实体类实现了 Serializable 接口（implements Serializable），代表这个类支持 Java 序列化 / 反序列化。
     * serialVersionUID 是版本校验标识：
     * 对象序列化（转字节流）存入 Redis、传输、本地缓存时，会带上这个编号；
     * 反序列化（把字节转回 Java 对象）时，会对比流里的版本号和当前类的编号；
     * 编号一致：正常转成对象；编号不一致：直接抛 InvalidClassException 序列化异常。
     * 2. 为什么要写 1L
     * 如果不手动定义 serialVersionUID，JDK 会根据类结构自动随机生成一个；
     * 后续你修改实体（增删字段、改字段名），自动生成的版本号会变化，旧序列化数据无法解析；
     * 手动固定写 1L，不管实体怎么改，版本号不变，兼容旧缓存、旧传输数据。
     * 3. 项目里什么时候会用到序列化
     * 你的漫画平台场景：
     * Redis 缓存实体对象；
     * 接口远程传输对象；
     * 会话 Session 存储实体。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    @TableId(value = "contentid", type = IdType.AUTO)
    private Integer contentid;

    /**
     * 章节ID
     */
    @TableField("chapterid")
    private Integer chapterid;

    /**
     * 漫画图片URL
     */
    @TableField("imgurl")
    private String imgurl;

    /**
     * 显示顺序
     */
    @TableField("displayorder")
    private Integer displayorder;

    /**
     * 显示状态
     */
    @TableField("status")
    private String status;

    /**
     * 发布日期
     */
    @TableField("releasedate")
    private LocalDateTime releasedate;
}
