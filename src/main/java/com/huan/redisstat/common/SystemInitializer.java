package com.huan.redisstat.common;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONArray;
import com.huan.redisstat.persistence.entities.Cluster;
import com.huan.redisstat.service.ClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * @author lihuan
 * 系统初始化程序
 */

@Slf4j
@Component
public class SystemInitializer {

    @Autowired
    ClusterService clusterService;

    @PostConstruct
    public void init() throws Exception {
        log.info("System Init...");
        initRedisCluster();
        initNavNodes();
    }

    /**
     * 初始化导航节点
     *
     * @throws IOException
     */
    public void initNavNodes() {
        try {
            ClassPathResource resource = new ClassPathResource("menu-data.json");

            log.info("初始化导航节点");
            String data = IoUtil.readUtf8(resource.getInputStream());
            NavHelper.init(JSONArray.parseArray(data, NavNode.class));
        } catch (Exception e) {
            log.error("初始化导航节点", e);
        }
    }

    /**
     * redis 初始化
     */
    private void initRedisCluster() {
        log.info("初始化Redis集群");
        List<Cluster> clusters = clusterService.selectAllCluster();
        RedisClusterStore.init(clusters);
    }

}
