package com.huan.redisstat.common;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {

    T selectResultMapByPrimaryKey(@Param("id") Object id);

    List<T> selectResultMapByExample(Example example);



}
