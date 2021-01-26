package com.huan.redisstat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author lihuan
 * 服务器向浏览器发送的此类消息。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage<T> {
    private int type;

    private T data;

}