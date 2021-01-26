package com.huan.redisstat.task;

/**
 * Created by lihuan on 21/1/21.
 */

import com.alibaba.fastjson.JSON;
import com.huan.redisstat.common.RedisClusterStore;
import com.huan.redisstat.model.RedisNode;
import com.huan.redisstat.model.WsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WebSocketTask {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Scheduled(fixedDelayString = "${job.push.stats}000")
    public void pushRedisStats() throws Exception {

        Map<String, List<String>> monitorActiveRedisClient = RedisClusterStore.getMonitorActiveRedisClient();

        monitorActiveRedisClient.forEach((k, v) -> {
            if (v.size() > 0) {
                RedisNode redisNode = RedisClusterStore.getNodeMapping().get(k);

                simpMessagingTemplate.convertAndSend("/topic/" + k,
                        JSON.toJSONString(new WsMessage(1, redisNode.info())));
                log.debug("ws发生推送消息 redisNode:{}", k);
            } else {
                log.debug("没有活跃用户不发生推送消息 redisNode:{}", k);

            }

        });


    }

}
