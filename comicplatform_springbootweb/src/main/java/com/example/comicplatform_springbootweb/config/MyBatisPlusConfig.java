package com.example.comicplatform_springbootweb.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus 配置类
 * 配置分页插件
 *
 * @Bean
 * 将方法返回的拦截器对象交给 Spring 容器管理，全局生效。
 * MybatisPlusInterceptor
 * MyBatis-Plus 统一拦截器，用来装载各类内置插件（分页、乐观锁、防全表更新等）。
 * PaginationInnerInterceptor 分页插件（核心作用）
 * MyBatis-Plus 自带分页插件，不加这个插件分页查询会失效：
 * 不加插件：调用 page(pageParam, queryWrapper) 查询时，只会查全部数据，不会自动拼接 limit 分页 SQL；
 * 加上插件：自动识别分页参数，底层生成 MySQL 的 LIMIT offset,size 语句，实现分页。
 * DbType.mysql
 * 指定数据库类型为 MySQL，插件会根据对应数据库语法生成分页语句（不同数据库分页关键字不同：MySQL LIMIT / Oracle ROWNUM）。
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
