package com.huan.redisstat.configuration;

import com.huan.redisstat.common.RedisClusterStore;
import com.huan.redisstat.common.interceptors.WebSocketHandshakeInterceptor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;
import java.util.Map;

/**
 * websocket 配置及监听
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

    /**
     * 连接断开
     *
     * @param event
     */
    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        RedisClusterStore.removeActiveNodeClient(sessionId);
    }

    /**
     * 连接打开
     *
     * @param event
     */
    @EventListener
    public void onConnectedEvent(SessionConnectedEvent event) {
        GenericMessage<byte[]> message = (GenericMessage<byte[]>) event.getMessage();
        GenericMessage simpConnectMessage = (GenericMessage) message.getHeaders().get("simpConnectMessage");
        List<String> nodeList = (List) ((Map) simpConnectMessage.getHeaders().get("nativeHeaders")).get("node");
        String node = nodeList.stream().findFirst().get();
        String simpSessionId = ObjectUtils.toString(message.getHeaders().get("simpSessionId"));
        RedisClusterStore.addActiveNodeClient(node, simpSessionId);
    }


    /**
     * 打开消息订阅
     *
     * @param event
     */
    @EventListener
    public void onSubscribedEvent(SessionSubscribeEvent event) {
    }

    /**
     * 关闭订阅
     *
     * @param event
     */
    @EventListener
    public void onUnSubscribedEvent(SessionUnsubscribeEvent event) {

    }
}
