package com.huan.redisstat.service;

import com.huan.redisstat.persistence.entities.Cluster;

import java.util.List;

public interface ClusterService {

    List<Cluster> selectAllCluster();

    void addClusters(Cluster cluster);

    int deleteById(Long id);

    Cluster selectById(Long id);
}
