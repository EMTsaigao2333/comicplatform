package com.example.comicplatform_springbootweb;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

/**
 * 代码生成器模板
 *生成器好像会生成一个示例代码在.demos.web下,可以删除（如果没有这个包，生成的文件和com文件夹同级）
 *
 * 2026-07-05 黑炎
 * 生成的是 Chaptercontent （c 小写），正确应该是 ChapterContent （C 大写）。
 *原因 ：你的表名叫 t_chaptercontent ，没有下划线分隔 chapter 和 content 。
 * 配置里 naming(NamingStrategy.underline_to_camel) 只认下划线，
 * 所以 chaptercontent 被当作一个整体，变成了 Chaptercontent 。
 * 后续修改建议：数据库表名用下划线分隔，字段名用驼峰命名法，以防止申城错误。
 *
 */

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create(//连数据库，读表结构
                        "jdbc:mysql://localhost:3306/comicplatformdb?serverTimezone=Asia/Shanghai",
                        "root",
                        "your_password_here"
                )
                .globalConfig(builder -> builder//生成注释
                        .author("黑炎")
                        .outputDir("D:\\web\\comicplatform_springbootweb\\src\\main\\java")
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(builder -> builder
                        .parent("com.example.comicplatform_springbootweb")//生成位置
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml,
                                "D:\\web\\comicplatform_springbootweb\\src\\main\\resources\\mapper"
                        ))
                )
                .strategyConfig(builder -> builder
                        .addTablePrefix("t_")//去掉表名里的t_
                        .addInclude("t_chapter", "t_chaptercontent", "t_user", "t_bookshelf")//需要生成的表
                        .entityBuilder()
                        .enableLombok()//实体类用 @Data 代替手写 getter/setter，代码量减 70%
                        .enableTableFieldAnnotation()//🔑 关键！每个字段自动加 @TableField("comicid") ，解决你字段无下划线的问题
                        .naming(NamingStrategy.underline_to_camel)
                        .columnNaming(NamingStrategy.no_change)//.columnNaming(NamingStrategy.no_change)
                        .controllerBuilder()
                        .enableRestStyle()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                )
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}