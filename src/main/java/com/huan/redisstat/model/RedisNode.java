package com.huan.redisstat.model;

import com.google.common.base.Splitter;
import com.huan.redisstat.common.MyBeanUtils;
import com.huan.redisstat.persistence.entities.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Slowlog;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

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


    /**
     * redis与系统时间差
     */
    private long sysDiffSeconds = 0;

    /**
     * 上次执行时间
     */
    private long lastSeconds = 0;


    private Map<String, Double> oldCpuUseMap = new HashMap<>();

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


    /**
     * redis连接
     *
     * @return
     */
    public Jedis connect() {
        if (jedisPool == null) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(2);
            poolConfig.setMaxIdle(1);
            poolConfig.setMinIdle(1);
            poolConfig.setMaxWaitMillis(1000 * 1);
            jedisPool = new JedisPool(poolConfig, host, port, 1000);
        }
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    /**
     * 节点数据初始化
     */
    public synchronized void init() {
        if (sysDiffSeconds == 0) {
            Properties properties = info();
            long nowSeconds = Long.parseLong(StringUtils.defaultString(properties.getProperty("uptime_in_seconds"),
                    "0"));
            sysDiffSeconds = (System.currentTimeMillis() / 1000) - nowSeconds;
            role = ObjectUtils.defaultIfNull(properties.get("role"),"").toString();
        }
    }
    public Properties info() {
        Jedis jedis = null;
        try {
            jedis = connect();
            String redisInfo = jedis.info();
            Properties redisInfoProperties =
                    PropertiesLoaderUtils.loadProperties(new InputStreamResource(new ByteArrayInputStream(redisInfo.getBytes())));
            Map<String, Integer> sumMap = countDbSize(redisInfoProperties);
            redisInfoProperties.put("dbSize", sumMap.get("keys"));
            redisInfoProperties.put("limitExpiredKeys", sumMap.get("expires"));

            long nowSeconds = Integer.valueOf(redisInfoProperties.getProperty("uptime_in_seconds"));
            redisInfoProperties.put("used_cpu_sys_rate", calculate("used_cpu_sys_rate",
                    redisInfoProperties.getProperty("used_cpu_sys"), nowSeconds));
            redisInfoProperties.put("used_cpu_user_rate", calculate("used_cpu_user_rate",
                    redisInfoProperties.getProperty("used_cpu_user"), nowSeconds));
            redisInfoProperties.put("used_cpu_sys_children_rate", calculate("used_cpu_sys_children_rate",
                    redisInfoProperties.getProperty("used_cpu_sys_children"), nowSeconds));
            redisInfoProperties.put("used_cpu_user_children_rate", calculate("used_cpu_user_children_rate",
                    redisInfoProperties.getProperty("used_cpu_user_children"), nowSeconds));
            redisInfoProperties.put("sysTime", (sysDiffSeconds + nowSeconds));
            lastSeconds = nowSeconds;

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

    /**
     * 计算cpu平均使用率
     *
     * @param cpuTime
     * @param nowSeconds
     * @return
     */
    private String calculate(String key, String cpuTime, long nowSeconds) {
        double currUse = Double.valueOf(cpuTime);
        //第一次之后
        if (oldCpuUseMap.containsKey(key)) {

            double diffCpuTime = currUse - oldCpuUseMap.get(key);

            long diffSeconds = nowSeconds - lastSeconds;

            double cpuLoad = (nowSeconds > 0 && diffCpuTime > 0) ? (diffCpuTime / diffSeconds) : 0;

            if (diffCpuTime > 0) {

                log.debug("key:{}  cpuTime:{} oldTime:{} diffSeconds:{} diffCpuTime:{}  nowSeconds:{} cpuLoad:{}%",
                        key,
                        cpuTime,
                        oldCpuUseMap.get(key),
                        diffSeconds,
                        diffCpuTime,
                        nowSeconds,
                        cpuLoad);
            }
            oldCpuUseMap.put(key, currUse);

            return String.valueOf(cpuLoad);
        } else {

            oldCpuUseMap.put(key, currUse);
            return "";
        }


    }


    /**
     * 统计key总数
     *
     * @param properties
     * @return
     */
    private Map<String, Integer> countDbSize(Properties properties) {

        Map<String, Integer> sumMap =
                properties.entrySet().stream()
                        .filter(item -> String.valueOf(item.getKey()).indexOf("db") == 0)
                        .map(item -> split2MapAndGet(String.valueOf(item.getValue())))
                        .flatMap(m -> m.entrySet().stream())
                        .collect(Collectors.groupingBy(Map.Entry::getKey,
                                Collectors.summingInt(Map.Entry::getValue)
                        ));
//        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
//        Collectors.summingInt(item->item.values().

        return sumMap;
    }


    /**
     * 键值对对解析
     *
     * @param in
     * @return
     */
    private Map<String, Integer> split2MapAndGet(String in) {
        return Splitter.on(",").withKeyValueSeparator("=").split(in).entrySet().stream()
                .filter(item -> ArrayUtils.contains(new String[]{"keys", "expires"}, item.getKey()))
                .collect(Collectors.toMap(item -> item.getKey(), item -> Integer.parseInt(item.getValue())));
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

