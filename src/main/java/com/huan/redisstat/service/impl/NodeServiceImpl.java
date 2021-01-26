package com.huan.redisstat.service.impl;

import com.huan.redisstat.persistence.mapper.ClusterMapper;
import com.huan.redisstat.persistence.entities.Cluster;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import com.huan.redisstat.persistence.entities.Node;
import com.huan.redisstat.persistence.mapper.NodeMapper;
import com.huan.redisstat.service.NodeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class NodeServiceImpl implements NodeService {

    @Resource
    private NodeMapper nodeMapper;
    @Resource
    private ClusterMapper clusterMapper;

    @Override
    @Transactional
    public int deleteById(Long id) {
        Node node = nodeMapper.selectByPrimaryKey(id);
        List<Node> nodeList = nodeMapper.selectByClusterId(node.getClusterId());
        if (nodeList.size() == 1) {
            clusterMapper.updateByPrimaryKeySelective(new Cluster().setId(node.getClusterId()).setDataState(0));
        }
        return nodeMapper.updateByPrimaryKeySelective(new Node().setId(id).setDataState(0).setUpdateTime(new Date()));

    }

    @Override
    public int add(Node record) {
        return nodeMapper.insert(record.setDataState(1).setCreateTime(new Date()));
    }


}
