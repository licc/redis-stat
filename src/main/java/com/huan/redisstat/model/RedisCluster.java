package com.huan.redisstat.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Setter
@Getter
@ToString
@Accessors(chain = true)
public class RedisCluster {
    private Long id;
    private String name;
    private List<RedisNode> nodes;
    private List<RedisCluster> children;

    public RedisCluster() {
    }

    public RedisCluster(String name) {
        this.name = name;
    }


}
