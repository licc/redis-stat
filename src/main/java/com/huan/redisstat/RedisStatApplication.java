package com.huan.redisstat;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by lihuan on 2021/01/18.
 */
@EnableScheduling
@EnableCaching
@EnableWebSecurity
@SpringBootApplication(exclude = MybatisAutoConfiguration.class)
public class RedisStatApplication {
	static {
		System.setProperty("spring.config.name","application,jdbc,config");
	}
	public static void main(String[] args) {
		SpringApplication.run(RedisStatApplication.class, args);
	}
}
