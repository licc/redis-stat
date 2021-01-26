package com.huan.redisstat.configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan(basePackages = "com.huan.redisstat.persistence.mapper", sqlSessionFactoryRef = "mapSqlSessionFactory")
public class MybatisConfig {

	@Resource
	DataSource dataSource;

	@Bean(name = "mapSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml"));
		return sqlSessionFactoryBean.getObject();
	}

}
