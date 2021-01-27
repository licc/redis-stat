package com.huan.redisstat.common;

import com.huan.redisstat.model.RedisCluster;
import com.huan.redisstat.model.RedisNode;
import com.huan.redisstat.persistence.entities.Cluster;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *redis仓库
 */
@Slf4j
public class RedisClusterStore {
    /**
     * 有用户查看的redis数据
     */
    private static Map<String, List<String>> monitorActiveRedisClient = new ConcurrentHashMap<>();

    private static List<RedisCluster> redisClusters = new ArrayList<>();

    private static Map<String, RedisNode> nodeMapping = new HashMap<>();


    public static void init(List<Cluster> clusters) {

        clusters.stream().forEach(item -> {
            RedisClusterStore.redisClusters.add(new RedisCluster(item.getName())
                    .setId(item.getId()).setNodes(item.getNodes().stream().
                            map(node -> {
                                RedisNode redisNode = RedisNode.translateFromNode(node);
                                nodeMapping.put(node.getHost() + ":" + node.getPort(), redisNode);
                                redisNode.init();
                                return redisNode;
                            }).collect(Collectors.toList())));

        });


    }

    public static void refresh(Cluster cluster) {
        RedisClusterStore.redisClusters.add(new RedisCluster(cluster.getName())
                .setId(cluster.getId()).setNodes(cluster.getNodes().stream().
                        map(node -> {
                            RedisNode redisNode = RedisNode.translateFromNode(node);
                            nodeMapping.put(node.getHost() + ":" + node.getPort(), redisNode);
                            redisNode.init();
                            return redisNode;
                        }).collect(Collectors.toList())));

    }

    public static RedisNode getRedisNode(String host, int port) {
        return nodeMapping.get(host + ":" + port);
    }

    public static List<RedisCluster> getRedisClusters() {
        return redisClusters;
    }

    /**
     * 添加活跃节点
     *
     * @param node      节点地址
     * @param sessionId ws id
     */
    public static boolean addActiveNodeClient(String node, String sessionId) {
        monitorActiveRedisClient.putIfAbsent(node, new ArrayList<>());
        return monitorActiveRedisClient.get(node).add(sessionId);
    }

    /**
     * 删除下线的节点
     *
     * @param node      节点地址
     * @param sessionId ws id
     * @return
     */
    public static boolean removeActiveNodeClient(String node, String sessionId) {
        return monitorActiveRedisClient.get(node).remove(sessionId);
    }

    /**
     * 获取当前活跃的客户端
     *
     * @return
     */
    public static Map<String, List<String>> getMonitorActiveRedisClient() {
        return monitorActiveRedisClient;
    }

    /**
     * 删除下线的节点
     *
     * @param sessionId ws id
     * @return
     */
    public static boolean removeActiveNodeClient(String sessionId) {
        monitorActiveRedisClient.values().stream().forEach(item -> {

            item.remove(sessionId);

        });
        return true;
    }

    public static void setRedisClusters(List<RedisCluster> redisClusters) {
        RedisClusterStore.redisClusters = redisClusters;
    }

    public static Map<String, RedisNode> getNodeMapping() {
        return nodeMapping;
    }

    public static void setNodeMapping(Map<String, RedisNode> nodeMapping) {
        RedisClusterStore.nodeMapping = nodeMapping;
    }

    public static void closeJedisClient() {
        log.debug("开始释放redis连接...");
        for (String key : RedisClusterStore.getNodeMapping().keySet()) {
            try {
                RedisNode redisNode = RedisClusterStore.getNodeMapping().get(key);
                redisNode.closeConnect();
            } catch (Exception e) {
                log.debug(key + "连接释放失败！", e);
            } finally {
                log.debug(key + "连接已释放");
            }
        }
    }

}
