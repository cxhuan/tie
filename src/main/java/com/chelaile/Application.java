package com.chelaile;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement  // 启注解事务管理
@MapperScan(basePackages = {"com.chelaile.busms.mysql.dao","com.chelaile.auth.dao","com.chelaile.busms.presto.dao"},
		markerInterface = com.chelaile.base.BaseDao.class, annotationClass = Mapper.class)
@EnableScheduling
@ComponentScan
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


}