package com.huan.redisstat.service;

import com.huan.redisstat.persistence.entities.Node;

/**
 * @Author: lihuan
 * @Date: 2021/1/21 17:28
 * @Description:
 */
public interface NodeService {
    int deleteById(Long id);

    int add(Node record);


}


