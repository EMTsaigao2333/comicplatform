package com.example.comicplatform_springbootweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 漫画实体类
 *
 * 因为数据库字段没有下划线，这里得用注释，之后记得用下划线命名后续才方便查询
 *数据库字段是 comicid/comicId（没下划线），不符合 MP 默认驼峰转下划线规则；
 * @TableId只能标记主键
 * @TableField可以标记非主键字段
 *
 */

@TableName("t_comic")
public class Comic {

    /**
     * 漫画ID（主键，数据库自增）
     * @TableId：标记当前属性为表主键
     * value = "comicid"：指定数据库主键字段名为 comicid（无下划线）
     * type = IdType.AUTO：主键由数据库auto_increment自动生成
     */
    @TableId(value = "comicid", type = IdType.AUTO)
    private Integer comicId;

    /**
     * 漫画标题
     * @TableField：手动映射数据库字段 comictitle，关闭默认驼峰转下划线匹配
     */
    @TableField("comictitle")
    private String comicTitle;

    /**
     * 漫画作者
     * 数据库字段名与实体属性小写完全一致，可省略@TableField，加上更规范
     */
    @TableField("author")
    private String author;

    /**
     * 漫画分类
     */
    @TableField("category")
    private String category;

    /**
     * 漫画简介
     */
    @TableField("intro")
    private String intro;

    /**
     * 漫画所属国家
     */
    @TableField("country")
    private String country;

    /**
     * 漫画封面图片地址
     * 数据库真实字段：coverimg
     */
    @TableField("coverimg")
    private String coverImg;

    /**
     * 漫画搜索关键词
     */
    @TableField("keywords")
    private String keywords;

    /**
     * 创建时间
     */
    @TableField("createtime")
    private LocalDateTime createtime;


    /**
     * 漫画状态：已发布/已下架
     */
    @TableField("status")
    private String status;

    /**
     * 是否违规：0正常/1违规
     */
    @TableField("isviolation")
    private Integer isviolation;

    public Integer getComicId() {
        return comicId;
    }

    public void setComicId(Integer comicId) {
        this.comicId = comicId;
    }

    public String getComicTitle() {
        return comicTitle;
    }

    public void setComicTitle(String comicTitle) {
        this.comicTitle = comicTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIsviolation() {
        return isviolation;
    }

    public void setIsviolation(Integer isviolation) {
        this.isviolation = isviolation;
    }

    @Override
    public String toString() {
        return "Comic{" +
                "comicId=" + comicId +
                ", comicTitle='" + comicTitle + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", intro='" + intro + '\'' +
                ", country='" + country + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", keywords='" + keywords + '\'' +
                ", createtime=" + createtime +
                ", status='" + status + '\'' +
                ", isviolation=" + isviolation +
                '}'+"\n";
    }
}
