package com.huan.redisstat.ui.vo;

import com.huan.redisstat.common.MyBeanUtils;
import com.huan.redisstat.model.RedisCluster;
import com.huan.redisstat.persistence.entities.Cluster;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lihuan
 * @Date: 2021/1/24 14:11
 * @Description:
 */
@Setter
@Getter
@ToString
@Accessors(chain = true)
public class ClusterVO {

    private Long id;

    private String name;

    private List<NodeVO> nodes;

    private List<RedisCluster> children;


    public static ClusterVO translateFromCluster(Cluster cluster) {
        return MyBeanUtils.copyAndReturn(cluster, new ClusterVO()).setNodes(cluster.getNodes().stream().map(item ->
                NodeVO.translateFromNode(item)).collect(Collectors.toList()));
    }

    public static Cluster translateFromClustersVO(ClusterVO clusterVO) {
        return MyBeanUtils.copyAndReturn(clusterVO, new Cluster()
        );
    }
}
