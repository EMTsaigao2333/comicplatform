package com.example.comicplatform_springbootweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.comicplatform_springbootweb.mapper")
public class ComicplatformSpringbootwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComicplatformSpringbootwebApplication.class, args);
    }

}
