package com.huan.redisstat.model;

import com.huan.redisstat.common.MyBeanUtils;
import com.huan.redisstat.persistence.entities.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Slowlog;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @Author: lihuan
 * @Date: 2021/1/24 14:11
 * @Description:
 */
@Setter
@Getter
@Slf4j
public class RedisNode {

    private Long id;

    private Long clusterId;

    /**
     * 节点名称
     */
    private String name;
    /**
     * 主机地址
     */
    private String host;
    /**
     * 端口
     */
    private int port;

    private String role;

    private JedisPool jedisPool;

    public RedisNode() {
    }

    public RedisNode(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public RedisNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static RedisNode translateFromNode(Node node) {
        return MyBeanUtils.copyAndReturn(node, new RedisNode());
    }


    public Jedis connect() {
        if (jedisPool == null) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(20);
            poolConfig.setMaxIdle(10);
            poolConfig.setMinIdle(5);
            poolConfig.setMaxWaitMillis(1000 * 2);
            jedisPool = new JedisPool(poolConfig, host, port, 1000);
        }
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    public Properties info() {
        Jedis jedis = null;

        try {
            jedis = connect();
            String redisInfo = jedis.info();
            Properties redisInfoProperties =
                    PropertiesLoaderUtils.loadProperties(new InputStreamResource(new ByteArrayInputStream(redisInfo.getBytes())));
            redisInfoProperties.put("dbSize", jedis.dbSize());
            role = redisInfoProperties.get("role").toString();
            return redisInfoProperties;
        } catch (Exception e) {
            log.error("获取 redis  host:{} port:{} info 失败", host, port, e);
        } finally {
            if (jedis != null) {
                jedis.close();

            }
        }
        return new Properties();

    }

    public Map<String, Object> configs() {
        Jedis jedis = null;

        Map<String, Object> configs = new HashMap<>(256);

        try {
            jedis = connect();
            List<String> configStrList = jedis.configGet("*");
            for (int i = 0; i < configStrList.size(); i++) {
                configs.put(configStrList.get(i), configStrList.get(++i));
            }
            return configs;
        } catch (Exception e) {
            log.error("获取 redis  host:{} port:{} info 失败", host, port, e);
        } finally {
            if (jedis != null) {
                jedis.close();

            }
        }
        return configs;

    }

    public List<Slowlog> slowLogs() {
        Jedis jedis = null;
        try {
            jedis = connect();
            List<Slowlog> slowlogs = jedis.slowlogGet();
            return slowlogs;
        } catch (Exception e) {
            log.error("获取 redis  host:{} port:{} info 失败", host, port, e);
        } finally {
            if (jedis != null) {
                jedis.close();

            }
        }
        return Collections.emptyList();

    }

    public void closeConnect() {
        jedisPool.close();
    }

    public String getRole() {
        return StringUtils.defaultIfBlank(role, "unknown");
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

}
