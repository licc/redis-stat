package com.huan.redisstat.service.impl;

import com.huan.redisstat.persistence.entities.Cluster;
import com.huan.redisstat.persistence.entities.Node;
import com.huan.redisstat.persistence.mapper.ClusterMapper;
import com.huan.redisstat.persistence.mapper.NodeMapper;
import com.huan.redisstat.service.ClusterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class ClusterServiceImpl implements ClusterService {

    @Resource
    private ClusterMapper clusterMapper;
    @Resource
    private NodeMapper nodeMapper;

    @Override
    public List<Cluster> selectAllCluster() {
        Example example = new Example(Cluster.class);
        example.createCriteria().andEqualTo(new Cluster().setDataState(1));
        example.orderBy("id").desc();
        return clusterMapper.selectResultMapByExample(example);
    }

    @Override
    @Transactional
    public void addClusters(Cluster app) {
        clusterMapper.insert(app.setCreateTime(new Date()).setDataState(1));
    }

    @Override
    @Transactional
    public int deleteById(Long id) {

        Example example = new Example(Node.class);
        example.createCriteria().andEqualTo(new Node().setClusterId(id));
        nodeMapper.updateByExampleSelective(new Node().setDataState(0), example);
        return clusterMapper.updateByPrimaryKey(new Cluster().setId(id).setDataState(0));

    }

    @Override
    public Cluster selectById(Long id) {
        return clusterMapper.selectResultMapByPrimaryKey(id);
    }


}
