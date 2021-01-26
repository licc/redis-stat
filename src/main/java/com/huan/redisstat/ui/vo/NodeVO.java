package com.huan.redisstat.ui.vo;

import com.huan.redisstat.common.RedisClusterStore;
import com.huan.redisstat.common.MyBeanUtils;
import com.huan.redisstat.model.RedisNode;
import com.huan.redisstat.persistence.entities.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Author: lihuan
 * @Date: 2021/1/24 14:15
 * @Description:
 */
@Setter
@Getter
@ToString
@Accessors(chain = true)
public class NodeVO {
    private Long id;

    private Long clusterId;

    private String name;

    private String host;

    private Integer port;


    private String role;

    public static NodeVO translateFromNode(Node node) {
        RedisNode redisNode = RedisClusterStore.getRedisNode(node.getHost(), node.getPort());
        return MyBeanUtils.copyAndReturn(node, redisNode != null ? new NodeVO().setRole(redisNode.getRole()) :
                new NodeVO());
    }

    public static Node translateFromNodeVO(NodeVO nodeVo) {
        return MyBeanUtils.copyAndReturn(nodeVo, new Node()
        );
    }
}
