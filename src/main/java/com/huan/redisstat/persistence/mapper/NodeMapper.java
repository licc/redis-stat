package com.huan.redisstat.persistence.mapper;

import com.huan.redisstat.persistence.entities.Node;
import com.huan.redisstat.common.MyMapper;

import java.util.List;

public interface NodeMapper extends MyMapper<Node> {
    List<Node> selectByClusterId(Long appId);
}